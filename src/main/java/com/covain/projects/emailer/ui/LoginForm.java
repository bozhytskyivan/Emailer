package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.ssl.SendMessageService;
import com.covain.projects.emailer.ui.customcomponents.InputWithHint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.BOLD;
import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.PLAIN;
import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Inset.INPUT;

public class LoginForm extends JFrame {

    private static Logger LOG = LoggerFactory.getLogger(LoginForm.class);

    private InputWithHint emailTextField;
    private JButton loginButton;
    private JPasswordField passwordTextField;

    public LoginForm() {
        super("Emailer: Login");
        LOG.info("Entered LoginForm");
        init();
        setVisible(true);

    }

    private void init() {
        setSize(380, 180);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel email = new JLabel("Email");
        email.setFont(BOLD);
        email.setBounds(20, 22, 80, 20);

        JLabel password = new JLabel("Password");
        password.setFont(BOLD);
        password.setBounds(20, 62, 80, 20);

        emailTextField = new InputWithHint("example@gmail.com", true);
        emailTextField.setFont(PLAIN);
        emailTextField.setBounds(100, 15, 210, 30);
        emailTextField.setMargin(INPUT);

        passwordTextField = new JPasswordField();
        passwordTextField.setFont(BOLD);
        passwordTextField.setBounds(100, 55, 210, 30);
        passwordTextField.setMargin(INPUT);

        loginButton = new JButton("Login");
        loginButton.setFont(email.getFont());
        loginButton.setBounds(85, 100, 230, 30);
        getRootPane().setDefaultButton(loginButton);
        loginButton.requestFocus();
        loginButton.addActionListener(e -> {
            String message = null;
            if (emailTextField.getText().length() == 0) {
               message = "Email is empty!";
            }
            if (passwordTextField.getPassword().length == 0) {
                message = "Password is empty!";
            }
            if (message == null && !isUserDataEnteredCorrectly(emailTextField.getText(), String.valueOf(passwordTextField.getPassword()))) {
                message = "Wrong Email or Password!";
            }
            if (message != null) {
                showExceptionDialog(message);
                return;
            }
            new MainForm(this).setVisible(true);
            setVisible(false);
        });

        add(email);
        add(password);
        add(emailTextField);
        add(passwordTextField);
        add(loginButton);

    }

    private void showExceptionDialog(String message) {
        ExceptionDialog
                .createNew(this, message)
                .display();

    }

    private boolean isUserDataEnteredCorrectly(String username, String password) {
        return SendMessageService.getNewService(username, password).authenticate();
    }

}
