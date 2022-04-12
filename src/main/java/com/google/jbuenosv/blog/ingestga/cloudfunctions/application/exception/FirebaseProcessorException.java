package com.google.jbuenosv.blog.ingestga.cloudfunctions.application.exception;

/**
 * Created by jbuenosv@google.com
 */
public class FirebaseProcessorException extends RuntimeException {

    public FirebaseProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public FirebaseProcessorException(String message) {
        super(message);
    }

    public FirebaseProcessorException(Throwable cause) {
        super(cause);
    }

}