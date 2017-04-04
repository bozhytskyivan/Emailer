package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.ssl.SendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private static Logger LOG = LoggerFactory.getLogger(LoginForm.class);

    private JTextField emailTextField;
    private JButton loginButton;
    private JPasswordField passwordTextField;
    private JPanel basePanel;

    public LoginForm() {
        super("EmailSender");
        LOG.info("Entered LoginForm");
        init();
        setVisible(true);
    }

    private void init() {
        MainForm mainForm = new MainForm(this);
        setContentPane(basePanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 200);
        setResizable(false);

        setDefaultLookAndFeelDecorated(true);
        loginButton.addActionListener(e -> {
            if (passwordTextField.getPassword().length == 0
                    || emailTextField.getText().length() == 0) {
                new ExceptionDialog(LoginForm.this, "All fields must be filled!");
                return;
            }
            System.out.println("username: " + emailTextField.getText());
            System.out.println("password: " + String.valueOf(passwordTextField.getPassword()));
            if (!SendMessageService
                    .getNewService(emailTextField.getText(), String.valueOf(passwordTextField.getPassword()))
                    .authenticate()) {
                new ExceptionDialog(LoginForm.this, "Some entered user data is wrong");
                return;
            }
            mainForm.setVisible(true);
            setVisible(false);
        });
    }
}
