package com.covain.projects.emailer.ssl;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class Sender implements Runnable {

    private String username;
    private String password;
    private Properties props;

    private boolean stopSending;

    public Sender() {
        initMailServer();
    }

    public Sender(String username, String password) {
        this.username = username;
        this.password = password;

        initMailServer();
    }

    private void initMailServer() {
        props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
    }

    public void send(String subject, String text, List<String> toEmails, List<String> attachments) throws MessagingException {
        stopSending = false;
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setSubject(subject);
        message.setText(text);
        for (String address : toEmails) {
            if (stopSending) {
                break;
            }
            System.out.println("Sending email to: " + address);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
            Transport.send(message);
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void stopSending() {
        System.out.println("Stopping sending");
        stopSending = true;
    }

    @Override
    public void run() {

    }

    public interface SenderListener {
        void messageSendingStartedTo(String recipientEmail);

        void messagesSendingFinished();
    }
}