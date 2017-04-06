package com.covain.projects.emailer.customcomponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.INPUT_TEXT_FONT;

public class InputWithHint extends JTextField implements FocusListener {

    private String hint;
    private boolean hintIsDisplayed;

    public InputWithHint() {
    }

    public InputWithHint(String text) {
        super(text);
    }

    public InputWithHint(String text, boolean isHint) {
        if (isHint) {
            hint = text;
            showHint();
        }
        setFont(INPUT_TEXT_FONT);
        addFocusListener(this);
        add(new JLabel("text"));

    }

    private void hideHint() {
        if (hintIsDisplayed) {
            setText(null);
            setForeground(Color.BLACK);
            hintIsDisplayed = false;
        }

    }

    private void showHint() {
        if (!hintIsDisplayed) {
            setText(hint);
            setForeground(Color.GRAY);
            hintIsDisplayed = true;
        }

    }

    @Override
    public String getText() {
        return hintIsDisplayed ? "" : super.getText();
    }

    @Override
    public void focusGained(FocusEvent e) {
        hideHint();

    }

    @Override
    public void focusLost(FocusEvent e) {
        if (getText().length() == 0) {
            showHint();
        }

    }
}
