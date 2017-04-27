package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Message;
import com.covain.projects.emailer.ssl.SendMessageService;
import com.covain.projects.emailer.ui.config.LocalizationKeys;
import com.covain.projects.emailer.utils.Localizer;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.BOLD;
import static com.covain.projects.emailer.ui.config.ComponentsConfigs.LOGGER;

public class SendingDialog extends AbstractDialog {

    private JButton cancelButton;
    private JLabel messageLabel;
    private JFrame owner;
    private SenderListener mSenderListener;
    private boolean continueSending;

    private static final int MESSAGE_HEIGHT = 30;
    private static final int MESSAGE_Y = 35;
    private static final int MARGIN = 5;

    private static int WINDOW_WIDTH = 380;
    private static int WINDOW_HEIGHT = 180;

    public SendingDialog(JFrame owner) {
        super(owner, Localizer.getString(LocalizationKeys.SENDING_TITLE));
        this.owner = owner;
        mSenderListener = (SenderListener) owner;

        init();

    }

    private void init() {
        addWindowListener(this);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        messageLabel = new JLabel();
        messageLabel.setFont(BOLD);
        messageLabel.setBounds(100, MESSAGE_Y, 210, MESSAGE_HEIGHT);

        cancelButton = new JButton(Localizer.getString(LocalizationKeys.CANCEL));
        cancelButton.setBounds(85, MESSAGE_Y + 65, 230, MESSAGE_HEIGHT);
        cancelButton.addActionListener(actionEvent -> {
            System.out.println("canceling sending");
            stop();
        });

        add(messageLabel);
        add(cancelButton);

    }

    public synchronized void start(Message message, int delay) {
        setVisible(true);
        continueSending = true;
        new Sender(message, delay).start();

    }

    private synchronized void stop() {
        if (continueSending) {
            continueSending = false;
            messageLabel.setText(Localizer.getString(LocalizationKeys.CANCELLING_MESSAGE));
        } else {
            dispose();
        }
    }


    class Sender extends Thread {

        int ONE_SEC = 1000;

        Message message;
        int delay;

        Sender(Message message, int delay) {
            this.message = message;
            this.delay = delay;
        }

        @Override
        public void run() {

            Iterator<String> recipientsIterator = message.getRecipients().iterator();
            int counter = 0;
            int recipientsCount = message.getRecipients().size();

            if (recipientsCount == 0) {
                ExceptionDialog.createNew(owner, Localizer.getString(LocalizationKeys.NO_SUCH_RECIPIENTS_MESSAGE)).display();
                LOGGER.error(Localizer.getString(LocalizationKeys.NO_SUCH_RECIPIENTS_MESSAGE));
                dispose();
                return;
            }

            while (recipientsIterator.hasNext() && continueSending) {
                String recipient = recipientsIterator.next();
                LOGGER.info("Sending message to: {}", recipient);

                try {
                    counter++;
                    messageLabel.setText(String.format(Localizer.getString(LocalizationKeys.SENDING_MESSAGE), counter, recipientsCount));
                    updateMessageLabelLength(messageLabel);
                    SendMessageService.getService().send(message.getSubject(), message.getBody(), recipient, message.getAttachments());
                    recipientsIterator.remove();
                    mSenderListener.onMessageSent(getRecipientsString(message.getRecipients()), recipient);
                } catch (Exception e) {
                    mSenderListener.onSendingFailed(getRecipientsString(message.getRecipients()), e);
                    continueSending = false;
                    return;
                }

                if (recipientsIterator.hasNext()) {
                    try {
                        messageLabel.setText(String.format(Localizer.getString(LocalizationKeys.WAITING_MESSAGE), delay));
                        Thread.sleep(delay * ONE_SEC);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (continueSending) {
                mSenderListener.onSendingFinished();
            } else {
                mSenderListener.onSendingCancelled();
            }
            dispose();
        }

    }

    private void updateMessageLabelLength(JLabel messageLabel) {
        Rectangle bounds = messageLabel.getBounds();
        if (messageLabel.getBounds() != null) {
            int fontSize = messageLabel.getFont().getSize();
            int messageWidth = fontSize * messageLabel.getText().length() / 2 + fontSize;
            if (WINDOW_WIDTH < (messageWidth + 2 * MARGIN)) {
                WINDOW_WIDTH = messageWidth + 2 * MARGIN;
                setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            }
            int left = (WINDOW_WIDTH - messageWidth) / 2;

            bounds.setSize(messageWidth, MESSAGE_HEIGHT);
            messageLabel.setBounds(left, MESSAGE_Y, messageWidth, MESSAGE_HEIGHT);
        }
    }

    private String getRecipientsString(List<String> input) {
        int cnt = 0;
        StringBuilder resultStringBuilder = new StringBuilder(input.size());
        for (String recipient : input) {
            cnt++;
            resultStringBuilder.append(recipient + (cnt != 0 && cnt % 4 == 0 ? "\n" : " "));
        }
        return resultStringBuilder.toString();

    }

    public interface SenderListener {

        void onSendingFinished();

        void onSendingFailed(String recipients, Exception e);

        void onMessageSent(String recipients, String sentTo);

        void onSendingCancelled();

    }

}
