package com.andreidodu.fromgtog.exception;

public class CloningDestinationException extends RuntimeException {

    public CloningDestinationException(String message) {
        super(message);
    }

    public CloningDestinationException(String message, Throwable e) {
        super(message, e);
    }
}
