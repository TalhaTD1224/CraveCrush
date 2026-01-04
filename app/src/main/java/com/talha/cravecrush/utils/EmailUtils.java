package com.talha.cravecrush.utils;

import android.util.Log;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtils {

    private static final String SENDER_EMAIL = "";
    private static final String SENDER_PASSWORD = "";

    private static final String SMTP_HOST = "smtp.gmail.com";

    private static final Executor executor = Executors.newSingleThreadExecutor();

    public interface EmailCallback {
        void onResult(boolean success);
    }

    public static void sendOrderConfirmation(String emailTo, String subject, String messageBody, EmailCallback callback) {
        executor.execute(() -> {
            boolean success = sendEmail(emailTo, subject, messageBody);
            if (callback != null) {
                callback.onResult(success);
            }
        });
    }

    private static boolean sendEmail(String to, String subject, String body) {
        Properties props = new Properties();
        // Switching to SSL on port 465
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            Log.d("EmailUtils", "Email sent successfully to " + to + " via SSL.");
            return true;

        } catch (Exception e) {
            Log.e("EmailUtils", "Error sending email to " + to, e);
            return false;
        }
    }
}