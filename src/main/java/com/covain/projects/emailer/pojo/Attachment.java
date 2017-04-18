package com.covain.projects.emailer.pojo;

import javax.swing.*;

/**
 * Created by ivbo1115 on 4/17/2017.
 */
public class Attachment {

    private JButton removeFileButton;
    private JLabel removeFileLabel;
    private String absoluteFilePath;

    public JButton getRemoveFileButton() {
        return removeFileButton;
    }

    public void setRemoveFileButton(JButton removeFileButton) {
        this.removeFileButton = removeFileButton;
    }

    public JLabel getRemoveFileLabel() {
        return removeFileLabel;
    }

    public void setRemoveFileLabel(JLabel removeFileLabel) {
        this.removeFileLabel = removeFileLabel;
    }

    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    public void setAbsoluteFilePath(String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }
}
