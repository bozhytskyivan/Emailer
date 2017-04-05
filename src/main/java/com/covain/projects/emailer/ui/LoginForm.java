package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.ssl.SendMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private static Logger LOG = LoggerFactory.getLogger(LoginForm.class);

    private JTextField emailTextField;
    private JButton loginButton;
    private JPasswordField passwordTextField;

    public LoginForm() {
        super("Emailer: Login");
        LOG.info("Entered LoginForm");
        init();
        setVisible(true);
    }

    private void init() {
        MainForm mainForm = new MainForm(this);
        setSize(380, 180);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel email = new JLabel("Email");
        email.setFont(new Font(email.getFont().getName(), email.getFont().getStyle(), 15));
        email.setBounds(20, 25, 80, 20);

        JLabel password = new JLabel("Password");
        password.setFont(email.getFont());
        password.setBounds(20, 65, 80, 20);

        emailTextField = new JTextField();
        emailTextField.setFont(new Font(email.getFont().getName(), Font.PLAIN, email.getFont().getSize()));
        emailTextField.setBounds(100, 15, 210, 30);

        JLabel hint = new JLabel("example@gmail.com");
        hint.setFont(email.getFont());
        hint.setBounds(0, 20, 80, 30);
        hint.setForeground(Color.GRAY);

        passwordTextField = new JPasswordField();
        passwordTextField.setFont(emailTextField.getFont());
        passwordTextField.setBounds(100, 55, 210, 30);

        loginButton = new JButton("Login");
        loginButton.setFont(email.getFont());
        loginButton.setBounds(80, 100, 240, 30);
        loginButton.addActionListener(e -> {
            if (passwordTextField.getPassword().length == 0 || emailTextField.getText().length() == 0) {
                ExceptionDialog
                        .createNew(LoginForm.this, "All fields must be filled!")
                        .display();
                return;
            }
            System.out.println("username: " + emailTextField.getText());
            System.out.println("password: " + String.valueOf(passwordTextField.getPassword()));
            if (!isUserDataEnteredCorrectly(emailTextField.getText(), String.valueOf(passwordTextField.getPassword()))) {
                ExceptionDialog
                        .createNew(LoginForm.this, "Some entered user data is wrong")
                        .display();
                return;
            }
            mainForm.setVisible(true);
            setVisible(false);
        });

        add(email);
        add(password);
        add(hint);
        add(emailTextField);
        add(passwordTextField);
        add(loginButton);

    }

    private boolean isUserDataEnteredCorrectly(String username, String password) {
        SendMessageService messageService = SendMessageService.getNewService(username, password);
        return true;// messageService.authenticate();
    }

}
