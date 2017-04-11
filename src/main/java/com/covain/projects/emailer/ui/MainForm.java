package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Message;
import com.covain.projects.emailer.ssl.SendMessageService;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainForm extends JFrame implements SendingDialog.SenderListener {

    private JPanel basePanel;
    private JTextField subject;
    private JTextArea emailBody;
    private JButton sendButton;
    private JTextArea recipients;
    private JButton openFileButton;
    private JLabel filePath;
    private JComboBox delayComboBox;

    private JFileChooser fileChooser = new JFileChooser();

    private int[] delays = new int[]{0, 1, 5, 10, 15, 20, 30, 60, 100, 120, 300};

    public MainForm(LoginForm loginForm) {
        super("Emailer: " + SendMessageService.getService().username());
        init();
    }

    private void init() {
        setContentPane(basePanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
        setResizable(false);
        setLocationRelativeTo(null);

        setDefaultLookAndFeelDecorated(true);
        sendButton.addActionListener(actionEvent -> sendMessages());
        openFileButton.addActionListener(new ChoseFileActionListener());
        delayComboBox.setEditor(new BasicComboBoxEditor());
        for (int i = 0; i < delays.length; i++) {
            delayComboBox.addItem(delays[i]);
        }
    }

    private void sendMessages() {
        setEnabled(false);
        List<String> files = "".equals(filePath.getText()) ? null : Arrays.asList(filePath.getText());
        new SendingDialog(this).start(new Message(subject.getText()
                        , emailBody.getText()
                        , files
                        , recipients.getText())
                , (int) delayComboBox.getSelectedItem());
    }

    @Override
    public void onSendingFinished() {
        ExceptionDialog
                .createNew(this, "Sending sucessfully finished")
                .display();
    }

    @Override
    public void onSendingFailed(String recipientsString) {
        recipients.setText(recipientsString);
        ExceptionDialog
                .createNew(this, "Sending failed.")
                .display();
    }

    @Override
    public void onUpdate(String recipientsString) {
        recipients.setText(recipientsString);
    }

    @Override
    public void onSendingCancelled() {
        ExceptionDialog
                .createNew(this, "Message sending cancelled")
                .display();
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
