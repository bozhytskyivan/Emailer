package com.covain.projects.emailer.ssl;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

public class SendMessageService {

    private String username;
    private Session session;

    private boolean authenticationPassed = false;

    private static SendMessageService INSTANCE = null;

    private SendMessageService(final String username, final String password) {
        this.username = username;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static SendMessageService getNewService(String username, String password) {
        INSTANCE = new SendMessageService(username, password);
        return INSTANCE;
    }

    public String username() {
        return INSTANCE.username;
    }

    public static SendMessageService getService() {
        return INSTANCE;
    }

    public void send(String subject, String text, String toEmail, List<String> attachments) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
        message.setSubject(subject);
        message.setText(text);
        if (null != attachments && !attachments.isEmpty()) {
            BodyPart messageBodyPart;
            final Multipart multipart = new MimeMultipart();
            for (String attachment : attachments) {
                messageBodyPart = new MimeBodyPart();
                DataSource dataSource = new FileDataSource(attachment);
                messageBodyPart.setDataHandler(new DataHandler(dataSource));
                multipart.addBodyPart(messageBodyPart);
            }
            message.setContent(multipart);
        }
        Transport.send(message);
    }

    public boolean authenticate() {
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect();
            authenticationPassed = true;
        } catch (MessagingException e) {
            authenticationPassed = false;
        }
        return authenticationPassed;
    }

}