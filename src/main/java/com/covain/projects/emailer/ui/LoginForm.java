package com.covain.projects.emailer.ui;

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
    private JLabel emailLabel;
    private JLabel passwordLabel;

    private MainForm mainForm;

    public LoginForm() {
        super("EmailSender");
        LOG.info("Entered LoginForm");
        System.out.println(LOG.isDebugEnabled());
        mainForm = new MainForm(this);
        setContentPane(basePanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(400, 200);
        setResizable(false);

        setDefaultLookAndFeelDecorated(true);
        loginButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (passwordTextField.getPassword().length == 0) {
                    new ExceptionDialog(LoginForm.this, "Password must be filled");
                    return;
                }
                mainForm.setCredentials(emailTextField.getText(), passwordTextField.getPassword());
                mainForm.setVisible(true);
                setVisible(false);
            }
        });
        setVisible(true);
    }
}
