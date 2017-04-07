package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Message;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.Iterator;

import static com.covain.projects.emailer.ui.config.ComponentsConfigs.DIALOG_TEXT_FONT;

public class SendingDialog extends AbstractDialog {

    public static final String SENDING_TEXT_PATTERN = "Sending message %d of %d";

    private JButton cancelButton;
    private JLabel messageLabel;
    private JFrame owner;
    private boolean continueSending;

    public SendingDialog(JFrame owner) {
        super(owner, "Emailer: Sending");
        this.owner = owner;

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
        messageLabel.setFont(DIALOG_TEXT_FONT);
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

    public synchronized void start(Message message) {
        setVisible(true);
        continueSending = true;
        new Sender(message).start();

    }

    private synchronized void stop() {
        continueSending = false;
    }

    @Override
    public void windowOpened(WindowEvent e) {}

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

        Message message;

        Sender(Message message) {
            this.message = message;
        }
        @Override
        public void run() {

            Iterator<String> recipientsIterator = message.getRecipients().iterator();
            int counter = 0;
            int recipientsCount = message.getRecipients().size();

            while (recipientsIterator.hasNext()) {
                String recipient = recipientsIterator.next();
                System.out.println("continueSending = " + continueSending);

                if (continueSending) {
                    try {
                    System.out.println("Sending message to: " + recipient);
                        counter++;
                        //SendMessageService.getService().send(messageLabel.getSubject(), messageLabel.getBody(), recipient, messageLabel.getAttachments());
                        messageLabel.setText(String.format(SENDING_TEXT_PATTERN, counter, recipientsCount));
                        if (counter == 3) {
                            throw new MessagingException("haha");
                        }
                    System.out.println("message to " + recipient + " successfully sent.");
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    StringBuilder remainedRecipients = new StringBuilder(message.getRecipients().size());
                        message.getRecipients().forEach((r) -> remainedRecipients.append(r + " "));
                        ((SenderListener) owner).onSendingFailed(remainedRecipients.toString());
                        return;
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    ((SenderListener) owner).onSendingCancelled();
                    dispose();
                }
                recipientsIterator.remove();
            }
            ((SenderListener) owner).onSendingFinished();

        }

    }

    public interface SenderListener {

        void onSendingFinished();

        void onSendingFailed(String recipients);

        void onSendingCancelled();

    }

}
