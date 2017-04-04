package com.covain.projects.emailer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ExceptionDialog extends JDialog implements WindowListener {

    private JFrame owner;
    private JLabel messageLabel;
    private JButton okButton;

    private String message;

    public ExceptionDialog(JFrame owner, String message) {
        super(owner);
        this.owner = owner;
        this.message = message;

        init();
    }

    private void init() {
        addWindowListener(this);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        messageLabel = new JLabel(message);
        okButton = new JButton("Ok");
        okButton.setSize(100, 50);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        setLayout(new GridLayout(4, 1));
        add(messageLabel);
        add(new JLabel());
        add(okButton);
        setSize(400, 200);
        setResizable(false);
        setVisible(true);
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

    @Override
    public void windowIconified(WindowEvent e) {
        System.out.println("Window iconified");
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        System.out.println("window deiconified");
    }

    @Override
    public void windowActivated(WindowEvent e) {
        System.out.println("window activated");
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        System.out.println("window deactivated");
    }
}
