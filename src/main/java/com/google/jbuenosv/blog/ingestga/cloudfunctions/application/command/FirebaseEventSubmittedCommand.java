package com.google.jbuenosv.blog.ingestga.cloudfunctions.application.command;

import com.google.jbuenosv.blog.ingestga.cloudfunctions.domain.FirebaseConversionEvent;

/**
 * Created by jbuenosv@google.com
 */
public class FirebaseEventSubmittedCommand implements Command {

    private FirebaseConversionEvent firebaseConversionEvent;

    public FirebaseEventSubmittedCommand() {
    }

    public FirebaseConversionEvent getFirebaseConversionEvent() {
        return firebaseConversionEvent;
    }

    public void setFirebaseConversionEvent(FirebaseConversionEvent firebaseConversionEvent) {
        this.firebaseConversionEvent = firebaseConversionEvent;
    }

}
