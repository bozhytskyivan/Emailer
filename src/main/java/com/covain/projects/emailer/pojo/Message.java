package com.covain.projects.emailer.pojo;

import com.covain.projects.emailer.utils.MailParser;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.List;

public class Message {
    private String subject;
    private String body;
    private List<String> attachments;
    private List<String> recipients;

    public Message(@Nullable String subject, @Nullable String body, @Nullable List<String> attachments, @NotNull String recipients) {
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
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

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipientsString) {
        this.recipients = MailParser.parseString(recipientsString);
    }
}
