package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.utils.MailParser;
import com.covain.projects.emailer.ssl.Sender;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class MainForm extends JFrame {

    private JPanel basePanel;
    private JTextField senderEmail;
    private JTextField subject;
    private JTextArea emailBody;
    private JButton sendButton;
    private JTextArea recipients;
    private JButton openFileButton;
    private JLabel filePath;

    private JFileChooser fileChooser = new JFileChooser();
    private File logFile = new File("log.txt");

    private String fileName;

    private LoginForm loginForm;
    private Sender sender = new Sender();

    public MainForm(LoginForm loginForm) {
        super("EmailSender");

        this.loginForm = loginForm;

        init();
    }

    private void init() {
        setContentPane(basePanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);

        setDefaultLookAndFeelDecorated(true);
        sendButton.addActionListener(new SendEmailActionListener());
        openFileButton.addActionListener(new ChoseFileActionListener());
    }

    private void sendMessages() throws MessagingException {
        System.out.println("Recipients: " + recipients.getText());
        sender.send(subject.getText(), emailBody.getText(), MailParser.parseString(recipients.getText()), Arrays.asList(filePath.getText()));
    }

    private String[] getRecipients(String input) {
        return input.split(" ");
    }

    private void sendEmails(Email email, String[] recipients) throws EmailException {
        try (FileWriter fileWriter = new FileWriter(logFile)) {
            for (String recipient : recipients) {
                fileWriter.write("Sending email to " + recipient + "...\n");
                email.addTo(recipient);
                fileWriter.write("Message sent. Id: " + email.send() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logInitialData() {
        try (FileWriter fileWriter = new FileWriter(logFile)) {
            fileWriter.append("sender email: " + senderEmail.getText() + "\n")
                    .append("subject: " + subject.getText() + "\n")
                    .append("recipients string: " + recipients.getText() + "\n")
                    .append("body: " + emailBody.getText() + "\n")
                    .flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCredentials(String email, char[] password) {
        sender.setUsername(email);
        sender.setPassword(String.valueOf(password));
    }

    private class ChoseFileActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int returnValue = fileChooser.showOpenDialog(MainForm.this);
            if (JFileChooser.APPROVE_OPTION == returnValue) {
                File selectedFile = fileChooser.getSelectedFile();
                fileName = selectedFile.getAbsolutePath();
                filePath.setText(fileName);
            }
        }
    }

    private class SendEmailActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                sendMessages();
            } catch (MessagingException e) {
                //new ExceptionDialog(MainForm.this, e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
