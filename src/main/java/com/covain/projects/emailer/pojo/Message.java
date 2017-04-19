package com.covain.projects.emailer.pojo;

import com.covain.projects.emailer.ui.MainForm;
import com.covain.projects.emailer.utils.MailParser;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Message {
    private String subject;
    private String body;
    private List<String> attachments;
    private List<String> recipients;

    public Message(@Nullable String subject, @Nullable String body, @Nullable List attachments, @NotNull String recipients) {
        this.subject = subject;
        this.body = body;
        if (attachments != null && attachments.size() > 0) {
            setAttachments(attachments);
        }
        setRecipients(recipients);
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List attachments) {
        if (attachments.get(0) instanceof String) {
            this.attachments = attachments;
        } else if (attachments.get(0) instanceof Attachment) {
            this.attachments = new ArrayList<>(MainForm.MAX_ATTACHMENTS_SIZE);
            for (int i = 0; i < attachments.size(); i++) {
                this.attachments.add(((Attachment) attachments.get(i)).getAbsoluteFilePath());
            }
        }
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipientsString) {
        this.recipients = MailParser.parseString(recipientsString);
    }
}
