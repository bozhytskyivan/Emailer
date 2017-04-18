package com.covain.projects.emailer.ssl;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.List;
import java.util.Properties;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.LOGGER;

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
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(text);
        multipart.addBodyPart(messageBodyPart);
        if (null != attachments && !attachments.isEmpty()) {
            for (String attachment : attachments) {
                messageBodyPart = new MimeBodyPart();
                DataSource dataSource = new FileDataSource(attachment);
                messageBodyPart.setDataHandler(new DataHandler(dataSource));
                messageBodyPart.setFileName(attachment.substring(attachment.lastIndexOf(File.separatorChar) + 1));
                multipart.addBodyPart(messageBodyPart);
            }
        }
        message.setContent(multipart);
        Transport.send(message);
    }

    public boolean authenticate() {
        try {
            Transport transport = session.getTransport("smtp");
            transport.connect();
            authenticationPassed = true;
            LOGGER.info("Logged in successfully");
        } catch (MessagingException e) {
            LOGGER.error("Login failed: {}", e);
            authenticationPassed = false;
        }
        return authenticationPassed;
    }

}