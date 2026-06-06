package com.mycompany.techstore.services;

import java.util.Arrays;
import java.util.Properties;
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

public class EmailService {

    private final Properties mailProps;
    private final Session mailSession;

    public EmailService() {
        this.mailProps = new Properties();

        String smtpHost = System.getenv("SMTP_HOST");
        String smtpPort = System.getenv("SMTP_PORT");
        String smtpAuth = System.getenv("SMTP_AUTH");
        String smtpStartTls = System.getenv("SMTP_START_TLS_ENABLE");
        String trustMailTLS = System.getenv("SMTP_TRUST_ALL");

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
                return new PasswordAuthentication(System.getenv("SMTP_USERNAME"), System.getenv("SMTP_PASSWORD"));
            }
        });
    }

    public String sendOtpEmail(String toEmail) throws MessagingException {
        String otp = String.valueOf((int) (Math.floor(100000 + Math.random() * 900000)));

        Message message = new MimeMessage(mailSession);
        try {
            message.setFrom(new InternetAddress(System.getenv("SMTP_USERNAME")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Verify your Tech Store account");
            String body = "Hello,\n\nYour verification code is: " + otp + "\n\nIf you didn't request this, ignore this email.";
            message.setText(body);

            Transport.send(message);
            Logger.getLogger(EmailService.class.getName()).log(Level.INFO, "OTP email sent to {0}", toEmail);
        } catch (MessagingException mex) {
            Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE, "Failed to send OTP email: " + mex.getMessage() + ", with email: " + Arrays.toString(message.getFrom()), mex);
            throw mex;
        }

        return otp;
    }
}
