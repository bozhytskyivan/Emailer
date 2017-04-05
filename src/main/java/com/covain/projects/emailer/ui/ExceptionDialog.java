package com.covain.projects.emailer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class ExceptionDialog extends AbstractDialog {

    private JFrame owner;
    private JLabel messageLabel;
    private JButton okButton;

    private String message;

    private ExceptionDialog(JFrame owner, String message) {
        super(owner, "Emailer: me");
        this.owner = owner;
        this.message = message;

        init();
    }

    public static ExceptionDialog createNew(JFrame owner, String message) {
        return new ExceptionDialog(owner, message);
    }

    private void init() {
        addWindowListener(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        messageLabel = new JLabel(message);
        okButton = new JButton("Ok");
        okButton.setSize(100, 50);
        okButton.addActionListener(e -> dispose());
        setLayout(new GridLayout(4, 1));
        add(messageLabel);
        add(new JLabel());
        add(okButton);
        setSize(400, 200);
        setResizable(false);
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
