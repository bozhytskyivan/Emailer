package com.covain.projects.emailer.ui;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public abstract class AbstractDialog extends JDialog implements WindowListener {

    private JFrame owner;


    public AbstractDialog(JFrame owner, String title) {
        super(owner, title);
        this.owner = owner;
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void windowOpened(WindowEvent e) {
        owner.setEnabled(false);
        owner.setAlwaysOnTop(false);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        enableOwner();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        enableOwner();
    }

    protected void enableOwner() {
        owner.setEnabled(true);
        owner.setAlwaysOnTop(true);
    }

}
