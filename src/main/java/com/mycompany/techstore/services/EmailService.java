package com.mycompany.techstore.services;

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

        this.mailProps.put("mail.smtp.host", System.getenv("SMTP_HOST"));
        this.mailProps.put("mail.smtp.port", System.getenv("SMTP_HOST"));
        this.mailProps.put("mail.smtp.auth", System.getenv("SMTP_AUTH"));
        this.mailProps.put("mail.smtp.starttls.enable", System.getenv("SMTP_START_TLS_ENABLE"));

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
            Logger.getLogger(EmailService.class.getName()).log(Level.SEVERE, "Failed to send OTP email: " + mex.getMessage(), mex);
            throw mex;
        }

        return otp;
    }
}
