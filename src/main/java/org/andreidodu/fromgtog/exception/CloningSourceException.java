package org.andreidodu.fromgtog.exception;

public class CloningSourceException extends RuntimeException {

    public CloningSourceException(String message) {
        super(message);
    }

    public CloningSourceException(String message, Throwable e) {
        super(message, e);
    }
}
