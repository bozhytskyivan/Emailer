package com.covain.projects.emailer.ui;

import com.covain.projects.emailer.pojo.Message;
import com.covain.projects.emailer.ssl.SendMessageService;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.Iterator;

public class SendingDialog extends AbstractDialog {

    private JButton cancelButton;
    private JFrame owner;
    private boolean continueSending;


    public SendingDialog(JFrame owner) {
        super(owner, "Emailer: Sending");
        this.owner = owner;

        init();

    }

    private void init() {
        addWindowListener(this);
        setSize(300, 300);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);
        cancelButton = new JButton("Cancel");
        cancelButton.setBounds(100, 100, 100, 20);

        cancelButton.addActionListener(actionEvent -> {
            System.out.println("canceling sending");
            stop();
        });
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
            while (recipientsIterator.hasNext()) {
                String recipient = recipientsIterator.next();
                System.out.println("continueSending = " + continueSending);
                if (continueSending) {
                    try {
                    System.out.println("Sending message to: " + recipient);
                    //SendMessageService.getService().send(message.getSubject(), message.getBody(), recipient, message.getAttachments());
                        counter++;
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
                }
                recipientsIterator.remove();
            }
            ((SenderListener) owner).onSendingFinished();

        }

    }

    public interface SenderListener {

        void onSendingFinished();

        void onSendingFailed(String recipients);

    }

}
