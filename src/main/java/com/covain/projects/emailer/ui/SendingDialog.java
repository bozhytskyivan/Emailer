package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Message;
import com.covain.projects.emailer.ssl.SendMessageService;
import com.covain.projects.emailer.ui.config.LocalizationKeys;
import com.covain.projects.emailer.utils.Localizer;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.WindowEvent;
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

    public SendingDialog(JFrame owner) {
        super(owner, Localizer.getString(LocalizationKeys.SENDING_TITLE));
        this.owner = owner;
        mSenderListener = (SenderListener) owner;

        init();

    }

    private void init() {
        addWindowListener(this);
        setSize(380, 180);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        messageLabel = new JLabel();
        messageLabel.setFont(BOLD);
        messageLabel.setBounds(100, 35, 210, 30);

        cancelButton = new JButton(Localizer.getString(LocalizationKeys.CANCEL));
        cancelButton.setBounds(85, 100, 230, 30);
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

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        enableOwner();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        enableOwner();
    }

    private void enableOwner() {
        if (!owner.isEnabled()) {
            owner.setEnabled(true);
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

            while (recipientsIterator.hasNext() && continueSending) {
                String recipient = recipientsIterator.next();
                LOGGER.info("Sending message to: {}", recipient);

                try {
                    counter++;
                    messageLabel.setText(String.format(Localizer.getString(LocalizationKeys.SENDING_MESSAGE), counter, recipientsCount));
                    SendMessageService.getService().send(message.getSubject(), message.getBody(), recipient, message.getAttachments());
                    recipientsIterator.remove();
                    mSenderListener.onMessageSent(getRecipientsString(message.getRecipients()), recipient);
                } catch (MessagingException e) {
                    e.printStackTrace();
                    mSenderListener.onSendingFailed(getRecipientsString(message.getRecipients()), e);
                    dispose();
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
