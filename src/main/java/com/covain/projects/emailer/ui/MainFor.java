package com.covain.projects.emailer.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.BOLD;
import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.PLAIN;

public class MainFor extends JFrame {

    public static int MAX_ATTACHMENTS_SIZE = 10;

    private JTextField mSubjectField;
    private JTextArea mRecipientsArea;
    private JButton mOpenFileButton;
    private JTextArea mMessageBodyArea;
    private JComboBox<String> mDelayComboBox;

    private List<String> attachments = new ArrayList<>(MAX_ATTACHMENTS_SIZE);
    private List<JButton> removeAttachmentButtons = new ArrayList<>(MAX_ATTACHMENTS_SIZE);
    private List<JLabel> attachmentNames = new ArrayList<>(MAX_ATTACHMENTS_SIZE);
    private JFileChooser fileChooser = new JFileChooser();

    private int width = 700;
    private int height = 600;
    private int sideMargin = 20;
    private int labelsWidth = 100;
    private int labelsHeight = 20;
    private int inputsLeftMargin = sideMargin + labelsWidth;
    private int inputsWidth = width - inputsLeftMargin - sideMargin;

    private int currentAttachmentsSize = 0;

    public MainFor() {
        setSize(width, height);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        JLabel subjectLabel = new JLabel("Subject");
        subjectLabel.setFont(BOLD);
        subjectLabel.setBounds(sideMargin, 30, labelsWidth, labelsHeight);

        mSubjectField = new JTextField();
        mSubjectField.setFont(PLAIN);
        mSubjectField.setBounds(inputsLeftMargin, 25, inputsWidth, 25);

        JLabel recipientsLabel = new JLabel("Recipients");
        recipientsLabel.setFont(BOLD);
        recipientsLabel.setBounds(sideMargin, 60, labelsWidth, labelsHeight);


        mRecipientsArea = new JTextArea();
        mRecipientsArea.setFont(PLAIN);
        mRecipientsArea.setBounds(inputsLeftMargin, 60, inputsWidth, 100);

        JScrollPane scrollPane = new JScrollPane(mRecipientsArea);
        scrollPane.setBounds(mRecipientsArea.getBounds());

        JLabel openFileLabel = new JLabel("Open file");
        openFileLabel.setFont(BOLD);
        openFileLabel.setBounds(sideMargin, 165, labelsWidth, labelsHeight);

        mOpenFileButton = new JButton("Open");
        mOpenFileButton.setMargin(new Insets(0, 0, 0, 0));
        mOpenFileButton.setBounds(inputsLeftMargin, 165, 50, 25);
        mOpenFileButton.addActionListener(new ChoseFileActionListener());

        JLabel mMessageBodyLabel = new JLabel("Body");
        mMessageBodyLabel.setFont(BOLD);
        mMessageBodyLabel.setBounds(sideMargin, 210, labelsWidth, labelsHeight);

        mMessageBodyArea = new JTextArea();
        mMessageBodyArea.setFont(PLAIN);
        mMessageBodyArea.setBounds(inputsLeftMargin, 210, inputsWidth, 300);

        JScrollPane bodyScrollPane = new JScrollPane(mMessageBodyArea);
        bodyScrollPane.setBounds(mMessageBodyArea.getBounds());

        add(subjectLabel);
        add(mSubjectField);
        add(recipientsLabel);
        add(scrollPane);
        add(openFileLabel);
        add(mOpenFileButton);
        add(mMessageBodyLabel);
        add(bodyScrollPane);

    }

    private void updateFiles() {
        int leftBorder = mOpenFileButton.getX() + mOpenFileButton.getWidth() + 5;
        int y = mOpenFileButton.getY();
        int attachmentWidth = (width - leftBorder - sideMargin) / 5;
        int fileNameWidth = attachmentWidth / 100 * 85;
        int deleteButtonWidth =  15;
        int componentHeight = labelsHeight - 5;

        leftBorder -= attachmentWidth;

        System.out.println(attachmentWidth);
        for (int i = 0; i < currentAttachmentsSize; i++) {
            String fullFileName = attachments.get(i);
            JLabel name = new JLabel(fullFileName.substring(fullFileName.lastIndexOf(File.separator) + 1));
            JButton deleteButton = new JButton("x");
            deleteButton.addActionListener((a) -> {
                System.out.println();
                int index = removeAttachmentButtons.indexOf(a.getSource());
                remove(removeAttachmentButtons.get(index));
                remove(attachmentNames.get(index));
                attachments.remove(index);
                removeAttachmentButtons.remove(index);
                attachmentNames.remove(index);
                currentAttachmentsSize--;
                updateFiles();
            });
            deleteButton.setMargin(new Insets(0, 0, 0, 0));
            int componentY = y;
            if (i % 2 == 0) {
                leftBorder += attachmentWidth + 2;
            } else {
                componentY += labelsHeight + 2;
            }
            name.setBounds(leftBorder, componentY, fileNameWidth, componentHeight);
            deleteButton.setBounds(leftBorder + attachmentWidth - deleteButtonWidth, componentY, deleteButtonWidth, componentHeight);

            removeAttachmentButtons.add(deleteButton);
            attachmentNames.add(name);
            add(name);
            add(deleteButton);
        }

    }

    private class ChoseFileActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (currentAttachmentsSize == MAX_ATTACHMENTS_SIZE) {
                ExceptionDialog
                        .createNew(MainFor.this, "There are already 10 files attached.")
                        .display();
                return;
            }
            int returnValue = fileChooser.showOpenDialog(MainFor.this);
            if (JFileChooser.APPROVE_OPTION == returnValue) {
                File selectedFile = fileChooser.getSelectedFile();
                attachments.add(selectedFile.getAbsolutePath());
                currentAttachmentsSize++;
                updateFiles();
            }
        }
    }

}
