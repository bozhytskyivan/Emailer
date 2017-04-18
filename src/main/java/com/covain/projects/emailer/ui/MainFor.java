package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Attachment;
import com.covain.projects.emailer.pojo.Message;
import com.covain.projects.emailer.ssl.SendMessageService;
import com.covain.projects.emailer.ui.config.LocalizationKeys;
import com.covain.projects.emailer.utils.Localizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.BOLD;
import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.PLAIN;

public class MainFor extends JFrame implements SendingDialog.SenderListener {

    public static final int MAX_ATTACHMENTS_SIZE = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(MainFor.class);
    private static final int[] DELAYS = new int[]{0, 1, 2, 5, 10, 15, 20, 30, 45, 60, 90, 120, 300};

    private JTextField mSubjectField;
    private JTextArea mRecipientsArea;
    private JButton mOpenFileButton;
    private JTextArea mMessageBodyArea;
    private JComboBox<Integer> mDelayComboBox;
    private JButton mSendButton;

    private JFileChooser fileChooser = new JFileChooser();

    private List<Attachment> attachments = new ArrayList<>(MAX_ATTACHMENTS_SIZE);
    private List<Attachment> visibleElements = new ArrayList<>(MAX_ATTACHMENTS_SIZE);

    private int width = 700;
    private int height = 600;
    private int sideMargin = 20;
    private int labelsWidth = 100;
    private int labelsHeight = 20;
    private int inputsLeftMargin = sideMargin + labelsWidth;
    private int inputsWidth = width - inputsLeftMargin - sideMargin;

    public MainFor() {
//        super("Emailer: " + SendMessageService.getService().username());

        setSize(width, height);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {

        JLabel subjectLabel = new JLabel(Localizer.getString(LocalizationKeys.SUBJECT));
        subjectLabel.setFont(BOLD);
        subjectLabel.setBounds(sideMargin, 30, labelsWidth, labelsHeight);

        mSubjectField = new JTextField();
        mSubjectField.setFont(PLAIN);
        mSubjectField.setBounds(inputsLeftMargin, 25, inputsWidth, 25);

        JLabel recipientsLabel = new JLabel(Localizer.getString(LocalizationKeys.RECIPIENTS));
        recipientsLabel.setFont(BOLD);
        recipientsLabel.setBounds(sideMargin, 60, labelsWidth, labelsHeight);


        mRecipientsArea = new JTextArea();
        mRecipientsArea.setFont(PLAIN);
        mRecipientsArea.setBounds(inputsLeftMargin, 60, inputsWidth, 100);

        JScrollPane scrollPane = new JScrollPane(mRecipientsArea);
        scrollPane.setBounds(mRecipientsArea.getBounds());

        JLabel openFileLabel = new JLabel(Localizer.getString(LocalizationKeys.OPEN_FILE));
        openFileLabel.setFont(BOLD);
        openFileLabel.setBounds(sideMargin, 165, labelsWidth, labelsHeight);

        mOpenFileButton = new JButton(Localizer.getString(LocalizationKeys.OPEN));
        mOpenFileButton.setMargin(new Insets(0, 0, 0, 0));
        mOpenFileButton.setBounds(inputsLeftMargin, 165, 75, 25);
        mOpenFileButton.addActionListener(new ChoseFileActionListener());

        JLabel mMessageBodyLabel = new JLabel(Localizer.getString(LocalizationKeys.MESSAGE_BODY));
        mMessageBodyLabel.setFont(BOLD);
        mMessageBodyLabel.setBounds(sideMargin, 230, labelsWidth, labelsHeight);

        mMessageBodyArea = new JTextArea();
        mMessageBodyArea.setFont(PLAIN);
        mMessageBodyArea.setBounds(inputsLeftMargin, 230, inputsWidth, 300);

        JScrollPane bodyScrollPane = new JScrollPane(mMessageBodyArea);
        bodyScrollPane.setBounds(mMessageBodyArea.getBounds());

        JLabel mDelayLabel = new JLabel(Localizer.getString(LocalizationKeys.DELAY));
        mDelayLabel.setFont(BOLD);
        mDelayLabel.setBounds(sideMargin, 480, labelsWidth, 20);

        mDelayComboBox = new JComboBox<>();
        mDelayComboBox.setFont(BOLD);
        mDelayComboBox.setBounds(sideMargin, 505, labelsWidth - 5, 25);
        mDelayComboBox.setEditable(true);
        for (int i = 0; i < DELAYS.length; i++) {
            mDelayComboBox.addItem(DELAYS[i]);
        }

        mSendButton = new JButton(Localizer.getString(LocalizationKeys.SEND));
        mSendButton.setFont(BOLD);
        mSendButton.setBounds(inputsLeftMargin, 535, inputsWidth, 25);
        mSendButton.addActionListener((a) -> sendMessages());

        add(subjectLabel);
        add(mSubjectField);
        add(recipientsLabel);
        add(scrollPane);
        add(openFileLabel);
        add(mOpenFileButton);
        add(mMessageBodyLabel);
        add(bodyScrollPane);
        add(mDelayLabel);
        add(mDelayComboBox);
        add(mSendButton);

    }

    private void updateFiles() {
        clearVisibleElements();
        repaint();
        if (attachments.size() > 0) {
            int leftBorder = mOpenFileButton.getX() + mOpenFileButton.getWidth() + 5;
            int y = mOpenFileButton.getY();
            int attachmentWidth = (width - leftBorder - sideMargin) / 4 - 5;
            int fileNameWidth = attachmentWidth - 17;
            int deleteButtonWidth = 15;
            int componentHeight = labelsHeight - 5;

            leftBorder -= attachmentWidth;

            int componentY = y;
            for (int i = 0; i < attachments.size(); i++) {
                Attachment attachment = attachments.get(i);
                JLabel fileName = attachment.getRemoveFileLabel();
                JButton removeFileButton = attachment.getRemoveFileButton();
                removeFileButton.setMargin(new Insets(0, 0, 0, 0));

                if (i % 3 == 0) {
                    componentY = y;
                    leftBorder += attachmentWidth + 5;
                } else {
                    componentY += labelsHeight + 2;
                }
                fileName.setBounds(leftBorder, componentY, fileNameWidth, componentHeight);
                removeFileButton.setBounds(leftBorder + attachmentWidth - deleteButtonWidth, componentY, deleteButtonWidth, componentHeight);

                add(fileName);
                add(removeFileButton);
            }
        }
        visibleElements.clear();
        visibleElements.addAll(attachments);
    }

    private void clearVisibleElements() {
        for (int i = 0; i < visibleElements.size(); i++) {
            remove(visibleElements.get(i).getRemoveFileButton());
            remove(visibleElements.get(i).getRemoveFileLabel());
        }
    }

    private void sendMessages() {
        setEnabled(false);
        String exception = checkUserInput();
        if (exception != null) {
            ExceptionDialog.createNew(this, exception).display();
            return;
        }
        new SendingDialog(this).start(new Message(mSubjectField.getText()
                        , mMessageBodyArea.getText()
                        , attachments
                        , mRecipientsArea.getText())
                , (Integer) mDelayComboBox.getEditor().getItem());
    }

    @Override
    public void onSendingFinished() {
        LOGGER.info("Sending successfully finished");
        ExceptionDialog
                .createNew(this, Localizer.getString(LocalizationKeys.SENDING_FINISHED))
                .display();
    }

    @Override
    public void onSendingFailed(String recipientsString, Exception e) {
        LOGGER.error("Message sending failed: {}", e);
        mRecipientsArea.setText(recipientsString);
        ExceptionDialog
                .createNew(this, Localizer.getString(LocalizationKeys.SENDING_FAILED_MESSAGE))
                .display();
    }

    @Override
    public void onMessageSent(String recipientsString, String sentTo) {
        LOGGER.info("Message successfully sent to {}", sentTo);
        mRecipientsArea.setText(recipientsString);
    }

    @Override
    public void onSendingCancelled() {
        LOGGER.warn("Sending cancelled by user");
        ExceptionDialog
                .createNew(this, Localizer.getString(LocalizationKeys.SENDING_CANCELLED))
                .display();
    }

    private String checkUserInput() {
        String result = null;
        String emptyString = "";
        if (emptyString.equals(mRecipientsArea.getText())) {
            result = Localizer.getString(LocalizationKeys.EMPTY_RECIPIENTS);
        }
        if (result == null && emptyString.equals(mMessageBodyArea.getText()) && attachments.isEmpty()) {
            result = Localizer.getString(LocalizationKeys.EMPTY_CONTENT_MESSAGE);
        }
        return result;
    }

    private class ChoseFileActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (attachments.size() == MAX_ATTACHMENTS_SIZE) {
                ExceptionDialog
                        .createNew(MainFor.this, Localizer.getString(LocalizationKeys.MAX_FILES_SELECTED))
                        .display();
                return;
            }
            int returnValue = fileChooser.showOpenDialog(MainFor.this);
            if (JFileChooser.APPROVE_OPTION == returnValue) {
                File selectedFile = fileChooser.getSelectedFile();
                String absoluteFilePath = selectedFile.getAbsolutePath();
                Attachment attachment = new Attachment();
                attachment.setRemoveFileButton(new JButton("x"));
                attachment.getRemoveFileButton().addActionListener((a) -> {
                    attachments.remove(attachment);
                    updateFiles();
                });
                attachment.setAbsoluteFilePath(absoluteFilePath);
                attachment.setRemoveFileLabel(new JLabel(absoluteFilePath.substring(absoluteFilePath.lastIndexOf(File.separator) + 1)));
                attachments.add(attachment);
                updateFiles();
            }
        }
    }

}
