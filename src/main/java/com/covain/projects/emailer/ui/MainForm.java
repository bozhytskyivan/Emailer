package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Message;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

public class MainForm extends JFrame implements SendingDialog.SenderListener {

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
        super("Emailer");

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
        setEnabled(false);
        new SendingDialog(this).start(new Message(subject.getText(), emailBody.getText(), Arrays.asList(filePath.getText()), recipients.getText()));
    }

    @Override
    public void onSendingFinished() {

    }

    @Override
    public void onSendingFailed(String recipients) {
        this.recipients.setText(recipients);

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
