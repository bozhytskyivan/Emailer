package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Message;
import com.covain.projects.emailer.ssl.SendMessageService;
import com.covain.projects.emailer.utils.MailParser;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainForm extends JFrame {

    private JPanel basePanel;
    private JTextField subject;
    private JTextArea emailBody;
    private JButton sendButton;
    private JTextArea recipients;
    private JButton openFileButton;
    private JLabel filePath;

    private LoginForm loginForm;

    private JFileChooser fileChooser = new JFileChooser();

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
        sendButton.addActionListener(actionEvent -> sendMessages());
        openFileButton.addActionListener(new ChoseFileActionListener());
    }

    private void sendMessages() {
//        setEnabled(false);
        new SendingDialog(this).start(new Message(subject.getText(), emailBody.getText(), Arrays.asList(filePath.getText()), recipients.getText()));
    }

    private class ChoseFileActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int returnValue = fileChooser.showOpenDialog(MainForm.this);
            if (JFileChooser.APPROVE_OPTION == returnValue) {
                File selectedFile = fileChooser.getSelectedFile();
                filePath.setText(selectedFile.getAbsolutePath());
            }
        }
    }
}
