package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.ssl.SendMessageService;
import com.covain.projects.emailer.ui.config.LocalizationKeys;
import com.covain.projects.emailer.ui.customcomponents.InputWithHint;
import com.covain.projects.emailer.utils.Localizer;

import javax.swing.*;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.BOLD;
import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.PLAIN;
import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Inset.INPUT;

public class LoginForm extends JFrame {

    private InputWithHint emailTextField;
    private JButton loginButton;
    private JPasswordField passwordTextField;

    public LoginForm() {
        super(Localizer.getString(LocalizationKeys.LOGIN_TITLE));
        init();
        setVisible(true);

    }

    private void init() {
        setSize(380, 180);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel email = new JLabel(Localizer.getString(LocalizationKeys.EMAIL));
        email.setFont(BOLD);
        email.setBounds(20, 22, 80, 20);

        JLabel password = new JLabel(Localizer.getString(LocalizationKeys.PASSWORD));
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

        loginButton = new JButton(Localizer.getString(LocalizationKeys.LOGIN));
        loginButton.setFont(email.getFont());
        loginButton.setBounds(85, 100, 230, 30);
        getRootPane().setDefaultButton(loginButton);
        loginButton.requestFocus();
        loginButton.addActionListener(e -> {
            String message = null;
            if (emailTextField.getText().length() == 0) {
               message = Localizer.getString(LocalizationKeys.EMPTY_EMAIL_MESSAGE);
            }
            if (passwordTextField.getPassword().length == 0) {
                message = Localizer.getString(LocalizationKeys.EMPTY_PASSWORD_MESSAGE);
            }
            if (message == null && !isUserDataEnteredCorrectly(emailTextField.getText(), String.valueOf(passwordTextField.getPassword()))) {
                message = Localizer.getString(LocalizationKeys.WRONG_EMAIL_OR_PASS_MESSAGE);
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
