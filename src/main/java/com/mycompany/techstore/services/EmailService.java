package com.mycompany.techstore.services;

import com.mycompany.techstore.Models.Objects.User;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;

public class EmailService {

    private final Properties mailProps;
    private final Session mailSession;
    private final ExecutorService emailExecutor;
    private final SecureRandom random;
    private final String smtpUsername;
    private final boolean configured;

    public EmailService() {
        this.random = new SecureRandom();
        this.mailProps = new Properties();

        String smtpHost = value("SMTP_HOST", "");
        String smtpPort = value("SMTP_PORT", "587");
        String smtpAuth = value("SMTP_AUTH", "true");
        String smtpStartTls = value("SMTP_START_TLS_ENABLE", "true");
        String trustMailTLS = value("SMTP_TRUST_ALL", "false");
        this.smtpUsername=value("SMTP_USERNAME", "");
        String smtpPassword=value("SMTP_PASSWORD", "");
        this.configured=!smtpHost.isBlank()&&!smtpUsername.isBlank()&&!smtpPassword.isBlank();

        this.mailProps.put("mail.smtp.host", smtpHost);
        this.mailProps.put("mail.smtp.port", smtpPort);
        this.mailProps.put("mail.smtp.auth", smtpAuth);
        this.mailProps.put("mail.smtp.connectiontimeout", "25000");
        this.mailProps.put("mail.smtp.timeout", "25000");

        // If using implicit SSL (SMTPS) on port 465, enable SSL instead of STARTTLS.
        if (smtpPort.equals("465")) {
            this.mailProps.put("mail.smtp.ssl.enable", "true");
            this.mailProps.put("mail.smtp.starttls.enable", "false");

            boolean trustAll = trustMailTLS.equalsIgnoreCase("true");
            if (trustAll) {
                Logger.getLogger(EmailService.class.getName()).log(Level.WARNING, "SMTP_TRUST_ALL is enabled — hostname verification and certificate name checks will be disabled. Use only for testing.");
                this.mailProps.put("mail.smtp.ssl.trust", smtpHost);
                this.mailProps.put("mail.smtp.ssl.checkserveridentity", "false");
            } else if (smtpHost != null) {
                this.mailProps.put("mail.smtp.ssl.trust", smtpHost);
            }
        } else {
            this.mailProps.put("mail.smtp.starttls.enable", (smtpStartTls != null) ? smtpStartTls : "false");
        }

        this.mailSession = Session.getInstance(this.mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername, smtpPassword);
            }
        });

        // Single-threaded daemon executor for sending emails asynchronously.
        this.emailExecutor = Executors.newSingleThreadExecutor((Runnable r) -> {
            Thread t = new Thread(r, "email-sender");
            t.setDaemon(true);
            return t;
        });
    }

    /*
      OTP Functions
     */
    
    public String generateOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public String sendOtpEmail(User user) throws MessagingException {
        if(!configured)throw new MessagingException("Email service is not configured");
        String otp = this.generateOtp(6);

        // Send the email asynchronously so callers don't block on network I/O.
        this.emailExecutor.submit(() -> {
            try {
                Message message = new MimeMessage(mailSession);
                message.setFrom(new InternetAddress(smtpUsername));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
                message.setSubject("Verify your Tech Store account");
                String body = """
                              Hi %s,
                              
                              Your email verification code is %s. It will be expire in an hour.
                              
                              If you didn't request this, ignore this email.
                              
                              Best regards
                              """.formatted(user.getFull_name(), otp);

                message.setText(body);
                Transport.send(message);
                Logger.getLogger(EmailService.class.getName()).log(Level.INFO, "OTP sent to %s".formatted(user.getEmail()));
            } catch (MessagingException mex) {
                Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE, "Failed to send OTP to email adress: " + mex.getMessage() + ", with email: " + user.getEmail(), mex);
            } catch (Exception ex) {
                Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE, "Unexpected error sending OTP to " + user.getEmail(), ex);
            }
        });

        return otp;
    }

    public boolean sendAccountChangeEmail(String email,String fullName,String subject,String detail){
        if(!configured){Logger.getLogger(EmailService.class.getName()).log(Level.WARNING,"Account notification was not sent because SMTP is not configured.");return false;}
        this.emailExecutor.submit(()->{
            try{
                Message message=new MimeMessage(mailSession);message.setFrom(new InternetAddress(smtpUsername));message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email));message.setSubject(subject);
                message.setText("Hi "+(fullName==null||fullName.isBlank()?"customer":fullName)+",\n\n"+detail+"\n\nIf you did not expect this change, please contact TechStore support.\n\nBest regards");
                Transport.send(message);
            }catch(Exception ex){Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE,"Failed to send account notification to "+email,ex);}
        });return true;
    }

    private static String value(String name,String fallback){String value=System.getenv(name);return value==null||value.isBlank()?fallback:value.trim();}

    // Optional: allow graceful shutdown of executor if the application wants to
    // close resources. Not invoked automatically.
    public void shutdown() {
        try {
            this.emailExecutor.shutdownNow();
        } catch (Exception ex) {
            Logger.getLogger(EmailService.class.getName()).log(Level.WARNING, "Error shutting down email executor", ex);
        }
    }
}
