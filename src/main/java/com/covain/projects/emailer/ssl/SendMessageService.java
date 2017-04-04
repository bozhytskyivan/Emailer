package com.covain.projects.emailer.ssl;

import com.sun.mail.util.MailConnectException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.net.ConnectException;
import java.util.List;
import java.util.Properties;

public class SendMessageService {

    private String username;
    private Session session;
    private Transport transport;
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
        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public static SendMessageService getNewService(String username, String password) {
        INSTANCE = new SendMessageService(username, password);
        return INSTANCE;
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
        BodyPart messageBodyPart;
        final Multipart multipart = new MimeMultipart();
        for (String attachment : attachments) {
            messageBodyPart = new MimeBodyPart();
            DataSource dataSource = new FileDataSource(attachment);
            messageBodyPart.setDataHandler(new DataHandler(dataSource));
            if (!attachments.isEmpty()) {
//                messageBodyPart.setFileName(attachment.substring(attachment.lastIndexOf(File.separatorChar)));
//                System.out.println("File name: " + attachment.substring(attachment.lastIndexOf(File.separatorChar) + 1));
            }
            multipart.addBodyPart(messageBodyPart);
        }
        message.setContent(multipart);
        Transport.send(message);
    }

    public boolean authenticate() {
        try {
            transport = session.getTransport("smtp");
            transport.connect();
            authenticationPassed = true;
        } catch (MessagingException e) {
            e.printStackTrace();
            authenticationPassed = false;
        }
        return authenticationPassed;
    }
}