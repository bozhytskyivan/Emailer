package com.covain.projects.emailer.exception;

public class NoSuchRecipientsException extends Exception {

    public NoSuchRecipientsException() {
    }

    public NoSuchRecipientsException(String message) {
        super(message);
    }
}
