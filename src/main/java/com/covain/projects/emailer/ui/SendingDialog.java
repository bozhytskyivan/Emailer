package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Message;
import com.covain.projects.emailer.ssl.SendMessageService;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.List;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.Fonts.BOLD;

public class SendingDialog extends AbstractDialog {

    public static final String SENDING_TEXT_PATTERN = "Sending message %d of %d";

    private JButton cancelButton;
    private JLabel messageLabel;
    private JFrame owner;
    private SenderListener mSenderListener;
    private boolean continueSending;

    public SendingDialog(JFrame owner) {
        super(owner, "Emailer: Sending");
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

        cancelButton = new JButton("Cancel");
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
            messageLabel.setText("Cancelling (Waiting for sending previous message)");
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

                try {
                    counter++;
                    messageLabel.setText(String.format(SENDING_TEXT_PATTERN, counter, recipientsCount));
                    SendMessageService.getService().send(message.getSubject(), message.getBody(), recipient, message.getAttachments());
                    recipientsIterator.remove();
                    mSenderListener.onUpdate(getRecipientsString(message.getRecipients()));
                } catch (MessagingException e) {
                    mSenderListener.onSendingFailed(getRecipientsString(message.getRecipients()));
                    dispose();
                    return;
                }

                try {
                    Thread.sleep(delay * ONE_SEC);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

        void onSendingFailed(String recipients);

        void onUpdate(String recipients);

        void onSendingCancelled();

    }

}
