package com.mycompany.techstore.Controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.techstore.Exceptions.AuthException;
import com.mycompany.techstore.Models.Objects.User;
import com.mycompany.techstore.services.AuthService;
import com.mycompany.techstore.services.EmailService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AuthController", urlPatterns = {"/auth"})
public class AuthController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Root Url
    private final String RootUrl;

    // Consistent OIDC attributes
    private final String OidcWellknown;
    private final String OidcClientId;
    private final String OidcClientSecret;
    private final String OidcScope;

    // OIDC attributes based on well-known ports
    private String OidcIssuer;
    private String OidcTokenEndpoint;
    private String OidcAuthEndpoint;
    private String OidcJwksUri;

    // Verify if OIDC is enabled
    private boolean isOidcEnabled;

    // Auth Service handler
    private transient final AuthService authService;
    private transient final EmailService emailService;

    // OTP Relax time between request (in second)
    private final int otpRelax = 30;

    public AuthController() {
        this.RootUrl = (System.getenv("ROOT_ENV") != null) ? System.getenv("ROOT_ENV") : "http://localhost:8080";

        this.OidcWellknown = System.getenv("OIDC_WELL_KNOWN");
        this.OidcClientId = System.getenv("OIDC_CLIENT_ID");
        this.OidcClientSecret = System.getenv("OIDC_CLIENT_SECRET");
        this.OidcScope = System.getenv("OIDC_SCOPE");

        String OidcIssuerProvided = System.getenv("OIDC_ISSUER");

        // Default values for external configuration
        this.isOidcEnabled = false;

        if (this.OidcClientId != null && this.OidcClientSecret != null && this.OidcWellknown != null && this.OidcScope != null && this.LoadOidcConfiguration()) {
            if (OidcIssuerProvided != null && this.OidcIssuer.equals(OidcIssuerProvided)) {
                this.isOidcEnabled = true;
            }
        }

        this.authService = new AuthService();

        if (System.getenv("SMTP_HOST") == null) {
            Logger.getLogger(EmailService.class.getName()).log(Level.WARNING, "SMTP_HOST is not configured; skipping OTP email.");
            this.emailService = null;
        } else {
            this.emailService = new EmailService();
        }
    }

    /*
     *  Authentication global functional methods
     *      - Those methods are used widely by many flows, and access within the Auth only
     */
    /////////////////////////////////////////////
    private boolean IsSignedIn(HttpServletRequest request) {
        User u = (User) request.getSession().getAttribute("loggedUser");
        return u != null;
    }

    private boolean LoadOidcConfiguration() {
        try {
            URL url = new URI(this.OidcWellknown).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Failed to fetch OIDC configuration. HTTP status: " + status);
            }

            try (JsonReader reader = Json.createReader(conn.getInputStream())) {
                JsonObject config = reader.readObject();

                this.OidcIssuer = config.getString("issuer");
                this.OidcTokenEndpoint = config.getString("token_endpoint");
                this.OidcJwksUri = config.getString("jwks_uri");
                this.OidcAuthEndpoint = config.getString("authorization_endpoint");

                Logger.getLogger(AuthController.class.getName()).log(Level.INFO, "OIDC configuration loaded successfully.");
                return true;
            }
        } catch (IOException | URISyntaxException e) {
            // Hard-coded exception handler
            Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, "Error loading OIDC configuration: " + e.getMessage(), e);
            return false;
        }
    }

    /*
     *  OIDC methods
     */
    /////////////////////////////////////////////
    private void HandleOidcLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.isOidcEnabled) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=OIDC+not+enabled.");
            return;
        }

        String state = UUID.randomUUID().toString();
        HttpSession session = request.getSession();

        session.setAttribute("oidc_state", state);

        String redirectUri = this.RootUrl + "/auth?action=oidc_callback";

        String authUrl = String.format(
                "%s?"
                + "client_id=%s&"
                + "redirect_uri=%s&"
                + "scope=%s&"
                + "response_type=code&"
                + "state=%s&"
                + "prompt=login",
                this.OidcAuthEndpoint,
                URLEncoder.encode(this.OidcClientId, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(this.OidcScope, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(state, StandardCharsets.UTF_8.toString())
        );

        response.sendRedirect(authUrl);
    }

    private void HandleOidcCallback(HttpServletRequest request, HttpServletResponse response) throws IOException, URISyntaxException {
        if (!this.isOidcEnabled) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=OIDC+not+enabled.");
            return;
        }

        String state = request.getParameter("state");
        String code = request.getParameter("code");

        HttpSession session = request.getSession();

        if (session == null || state == null || !state.equals(session.getAttribute("oidc_state"))) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=Invalid+OIDC+state.");
            return;
        }

        if (code == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=Authorization+code+is+missing.");
            return;
        }

        String redirectUri = this.RootUrl + "/auth?action=oidc_callback";

        // Exchange code for tokens
        URL tokenUrl = new URI(this.OidcTokenEndpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) tokenUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Set timeouts
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        String body = String.format(
                "grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
                URLEncoder.encode(code, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(this.OidcClientId, StandardCharsets.UTF_8.toString()),
                URLEncoder.encode(this.OidcClientSecret, StandardCharsets.UTF_8.toString())
        );

        conn.getOutputStream().write(body.getBytes(StandardCharsets.UTF_8));

        int status = conn.getResponseCode();
        if (status != 200) {
            response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=Token+endpoint+returned+" + status);
            return;
        }

        try (JsonReader jr = Json.createReader(conn.getInputStream())) {
            JsonObject jo = jr.readObject();
            String idToken = jo.getString("id_token", null);

            if (idToken == null) {
                response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=No+id_token+in+token+response");
                return;
            }

            try {
                URL jwksUrl = new URI(this.OidcJwksUri).toURL();

                ResourceRetriever resourceRetriever = new DefaultResourceRetriever(5000, 5000);
                JWKSource<SecurityContext> keySource = JWKSourceBuilder.create(jwksUrl, resourceRetriever).build();
                ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
                jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource));
                JWTClaimsSet claims = jwtProcessor.process(idToken, null);

                if (!claims.getAudience().contains(this.OidcClientId)) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=Token+audience+mismatch.");
                    return;
                }

                Date exp = claims.getExpirationTime();
                if (exp == null || exp.toInstant().isBefore(Instant.now())) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=Token+expired.");
                    return;
                }

                String email = claims.getStringClaim("email");
                String name = claims.getStringClaim("name");

                // Query or create user with email
                User user = this.authService.GetOrCreateUserOIDCSignIn(email, name);

                // Sign in locally (store sanitized copy without password)
                session.setAttribute("loggedUser", user);
                response.sendRedirect(request.getContextPath() + (user.getRole_id() == 1 ? "/admin/dashboard" : "/"));
            } catch (JOSEException | BadJOSEException | IOException | ParseException | AuthException ex) {
                Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=" + errorEx);
            }
        }
    }

    /*
     *  Email/Password methods
     */
    /////////////////////////////////////////////
    private void HandleSignIn(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            User user = this.authService.GetUserSignIn(email, password);
            if (user != null) {
                HttpSession session = request.getSession();
                session.setAttribute("loggedUser", user);

                if (!user.isIsVerified()) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=verify");
                    return;
                }

                response.sendRedirect(request.getContextPath() + (user.getRole_id() == 1 ? "/admin/dashboard" : "/"));
            } else {
                response.sendRedirect(request.getContextPath() + "/auth?action=denied");
            }
        } catch (AuthException ex) {
            Logger.getLogger(AuthController.class.getName()).log(Level.INFO, ex.getMessage());
            String reason = "This account is blocked or inactive".equals(ex.getMessage())
                    ? "blocked" : "invalid";
            response.sendRedirect(request.getContextPath() + "/auth?action=signin&reason=" + reason);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, null, ex);
            String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=" + errorEx);
        }
    }

    private void HandleSignUp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String name = request.getParameter("name");

        try {
            User user = this.authService.CreateUserSignIn(email, password, name);

            HttpSession session = request.getSession();
            session.setAttribute("loggedUser", user);

            response.sendRedirect(request.getContextPath() + "/auth?action=verify");
        } catch (AuthException | NoSuchAlgorithmException ex) {
            Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, null, ex);
            String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/auth?action=signup&error=" + errorEx);
        }
    }

    private void HandleResetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String otp = request.getParameter("otp");
        String newPwd = request.getParameter("newpwd");
        String repeatPwd = request.getParameter("repeatPwd");

        if (newPwd == null || repeatPwd == null || !newPwd.equals(repeatPwd)) {
            response.sendRedirect(request.getContextPath() + "/home?error=Error+input.+Please+validate+the+password.");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/home?error=Not+signed+in.");
            return;
        }

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            response.sendRedirect(request.getContextPath() + "/home?error=verify&error=Not+signed+in.");
            return;
        }

        if (otp == null || !session.getAttribute("otp").equals(otp)) {
            response.sendRedirect(request.getContextPath() + "/home?error=Invalid+OTP.");
            return;
        }

        try {
            boolean refreshAction = this.authService.UpdateUserPassword(logged.getEmail(), newPwd);
            if (refreshAction) {
                // Refresh User (temporary using OIDC)
                User userRefresh = this.authService.GetOrCreateUserOIDCSignIn(logged.getEmail(), logged.getFull_name());

                session.setAttribute("loggedUser", userRefresh);
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                response.sendRedirect(request.getContextPath() + "/home?error=Failed+to+update+password.");
            }
        } catch (AuthException | NoSuchAlgorithmException ex) {
            Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/home?error=" + errorEx);
        }
    }

    /*
     * OTP methods
     * It stores in session attribute, with contain 3 attributes
     * - otp: the OTP code itself
     * - otpReq: the OTP last request time
     * - otpExpire: the OTP expiration time
     */
    /////////////////////////////////////
    private void SetOTP(HttpServletRequest request) throws MessagingException, AuthException {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new AuthException(-1, "No active session");
        }

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new AuthException(-1, "Not logged in");
        }

        if (this.emailService == null) {
            throw new AuthException(-1, "Email service is not configured");
        }

        String otp = (String) session.getAttribute("otp");
        LocalDateTime curr = LocalDateTime.now();
        LocalDateTime lastReq = (LocalDateTime) session.getAttribute("otpReq");
        LocalDateTime expireReq = (LocalDateTime) session.getAttribute("otpExpire");

        // If no previous OTP, or previous OTP expired, create a fresh one
        if (otp == null || expireReq == null || curr.isAfter(expireReq)) {
            String newOtp = this.emailService.sendOtpEmail(logged);
            session.setAttribute("otp", newOtp);
            session.setAttribute("otpReq", curr);
            session.setAttribute("otpExpire", curr.plusHours(1));
            return;
        }

        // In case otp already exists - check if still within otp relax window
        if (lastReq != null && curr.isBefore(lastReq.plusSeconds(this.otpRelax))) {
            throw new AuthException(-1, "Requesting OTP too frequently. Please wait %s seconds.".formatted(this.otpRelax));
        }

        String newOtp = this.emailService.sendOtpEmail(logged);
        session.setAttribute("otp", newOtp);
        session.setAttribute("otpReq", curr);
        session.setAttribute("otpExpire", curr.plusHours(1));
    }

    private boolean VerifyOTP(HttpServletRequest request, String expectedOtp) throws AuthException {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new AuthException(-1, "No active session");
        }

        User logged = (User) session.getAttribute("loggedUser");
        if (logged == null) {
            throw new AuthException(-1, "Not logged in");
        }

        String otp = (String) session.getAttribute("otp");
        if (otp == null || expectedOtp == null) {
            return false;
        }

        LocalDateTime curr = LocalDateTime.now();
        LocalDateTime expireReq = (LocalDateTime) session.getAttribute("otpExpire");

        if (expireReq == null || curr.isAfter(expireReq)) {
            return false;
        }

        // Use constant-time comparison to mitigate timing attacks
        return MessageDigest.isEqual(otp.getBytes(StandardCharsets.UTF_8), expectedOtp.getBytes(StandardCharsets.UTF_8));
    }

    /*
      GET/POST methods
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (request.getParameter("action")) {
            // Default action on call
            case null -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin");
                }
            }
            // Regular Password Sign-up
            case "signin" -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/Login.jsp").forward(request, response);
                }
            }
            case "signup" -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/CreateAccount.jsp").forward(request, response);
                }
            }
            case "verify" -> {
                HttpSession session = request.getSession(false);
                if (session == null) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin");
                    return;
                }

                User logged = (User) session.getAttribute("loggedUser");
                if (logged == null) {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin");
                    return;
                }

                // OIDC users (no password) or already verified should be redirected home
                if (logged.getPassword() == null || logged.isIsVerified()) {
                    response.sendRedirect(request.getContextPath() + "/");
                    return;
                }

                // If SMTP is not configured, auto-verify the user and redirect home
                if (System.getenv("SMTP_HOST") == null) {
                    try {
                        boolean verifyStatus = this.authService.VerifyEmail(logged.getEmail());
                        if (verifyStatus) {
                            User userRefresh = this.authService.GetUserByEmail(logged.getEmail());
                            session.setAttribute("loggedUser", userRefresh);
                            response.sendRedirect(request.getContextPath() + "/");
                            return;
                        } else {
                            Logger.getLogger(AuthController.class.getName()).log(Level.WARNING, "SMTP_HOST not set and auto-verification failed for {0}", logged.getEmail());
                        }
                    } catch (AuthException ex) {
                        Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, null, ex);
                        response.sendRedirect(request.getContextPath() + "/home?error=Internal+server+error+during+auto-verification.");
                        return;
                    }
                }
                // Attempt to send OTP email only if EmailService is available; log failures but continue to verification page
                if (this.emailService != null) {
                    try {
                        this.SetOTP(request);
                    } catch (MessagingException mex) {
                        Logger.getLogger(AuthController.class.getName()).log(Level.WARNING, "Failed to send OTP email: " + mex.getMessage(), mex);
                    } catch (AuthException ex) {
                        Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                        String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                        response.sendRedirect(request.getContextPath() + "/home?error=" + errorEx);
                        return;
                    }
                } else {
                    Logger.getLogger(AuthController.class.getName()).log(Level.INFO, "EmailService disabled; not sending OTP email.");
                }

                request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/VerifyOTP.jsp").forward(request, response);
            }
            // OIDC
            case "oidc_signin" -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    if (this.isOidcEnabled) {
                        this.HandleOidcLogin(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=OIDC+is+not+enabled.");
                    }
                }
            }
            case "oidc_callback" -> {
                try {
                    if (this.isOidcEnabled) {
                        this.HandleOidcCallback(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=OIDC+is+not+enabled.");
                    }
                } catch (URISyntaxException ex) {
                    Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            // Reset password
            case "resetpwd" -> {
                if (this.IsSignedIn(request)) {
                    try {
                        this.SetOTP(request);
                    } catch (MessagingException mex) {
                        Logger.getLogger(AuthController.class.getName()).log(Level.WARNING, "Failed to send OTP email: " + mex.getMessage(), mex);
                    } catch (AuthException ex) {
                        Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                        String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                        response.sendRedirect(request.getContextPath() + "/home?error=" + errorEx);
                        return;
                    }
                    request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/ResetPwd.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=Not+signed+in.");
                }
            }
            // Logout and end session
            case "logout" -> {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/");
            }
            // Default action
            default -> {
                if (this.IsSignedIn(request)) {
                    response.sendRedirect(request.getContextPath() + "/");
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin");
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        switch (request.getParameter("action")) {
            case "signin" -> {
                if (!this.IsSignedIn(request)) {
                    this.HandleSignIn(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=Already+signed+in.");
                }
            }
            case "signup" -> {
                if (!this.IsSignedIn(request)) {
                    this.HandleSignUp(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/auth?action=signin&error=Already+signed+in.");
                }
            }
            case "resetpwd" -> {
                if (this.IsSignedIn(request)) {
                    this.HandleResetPassword(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/home?error=Not+signed+in.");
                }
            }
            case "verify" -> {
                HttpSession session = request.getSession(false);
                if (session == null) {
                    response.sendRedirect(request.getContextPath() + "/home?error=Not+signed+in.");
                    return;
                }

                User logged = (User) session.getAttribute("loggedUser");
                if (logged == null) {
                    response.sendRedirect(request.getContextPath() + "/home?error=Not+signed+in.");
                    return;
                }

                if (logged.getPassword() == null || logged.isIsVerified()) {
                    response.sendRedirect(request.getContextPath() + "/");
                    return;
                }

                try {
                    String expected = (String) session.getAttribute("otp");
                    if (expected == null || !this.VerifyOTP(request, expected)) {
                        request.setAttribute("error", "Invalid verification code. Please try again.");
                        response.sendRedirect(request.getContextPath() + "/auth?action=verify");
                        return;
                    }

                    boolean verifyStatus = this.authService.VerifyEmail(logged.getEmail());
                    if (verifyStatus) {
                        // Refresh user and update session
                        User user = this.authService.GetUserByEmail(logged.getEmail());
                        session.setAttribute("loggedUser", user);
                        session.removeAttribute("otp");
                        response.sendRedirect(request.getContextPath() + "/");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/home?error=Failed+to+verify+email.");
                    }
                } catch (AuthException ex) {
                    Logger.getLogger(AuthController.class.getName()).log(Level.SEVERE, null, ex);
                    String errorEx = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
                    response.sendRedirect(request.getContextPath() + "/home?error=" + errorEx);
                }
            }
            default -> {
                response.setStatus(404);
                request.getRequestDispatcher("/WEB-INF/JSPViews/AuthView/Denied.jsp").forward(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        this.emailService.shutdown();
    }
}
