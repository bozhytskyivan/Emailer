package com.covain.projects.emailer.ui;

import javax.swing.*;
import java.awt.event.WindowEvent;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.PLAIN;

public class ExceptionDialog extends AbstractDialog {

    private JFrame owner;
    private JLabel messageLabel;
    private JButton okButton;

    private String message;

    private ExceptionDialog(JFrame owner, String message) {
        super(owner, "Emailer: action failed");
        this.owner = owner;
        this.message = message;

        init();
    }

    public static ExceptionDialog createNew(JFrame owner, String message) {
        return new ExceptionDialog(owner, message);
    }

    private void init() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(380, 180);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);
        addWindowListener(this);

        messageLabel = new JLabel(message);
        messageLabel.setFont(PLAIN);
        int messageLength = messageLabel.getFont().getSize() * message.length() / 2;
        messageLabel.setBounds((getWidth() - messageLength) / 2, 35, messageLength, 30);

        okButton = new JButton("Ok");
        okButton.setBounds(140, 100, 100, 30);
        okButton.addActionListener(e -> dispose());

        add(messageLabel);
        add(okButton);
    }

    @Override
    public void windowOpened(WindowEvent e) {
        System.out.println("Window opened");
        owner.setEnabled(false);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("Window closing");
        if (!owner.isEnabled()) {
            owner.setEnabled(true);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        System.out.println("Window closed");
        if (!owner.isEnabled()) {
            owner.setEnabled(true);
        }
    }

    public void display() {
        setVisible(true);
    }

}
