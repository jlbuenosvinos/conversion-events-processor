package com.google.jbuenosv.blog.ingestga.cloudfunctions.application.process;

import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.command.FirebaseEventSubmittedCommand;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.command.FirebaseEventSubmittedCommandHandler;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.domain.FirebaseConversionEvent;

/**
 * Created by jbuenosv@google.com
 */
public class FirebaseConversionEventProcessManagerImpl implements FirebaseConversionEventProcessManager {

    FirebaseEventSubmittedCommandHandler firebaseEventSubmittedCommandHandler;
    FirebaseEventSubmittedCommand firebaseEventSubmittedCommand;

    /**
     * Default constructor
     */
    public FirebaseConversionEventProcessManagerImpl() {
        this.firebaseEventSubmittedCommandHandler = new FirebaseEventSubmittedCommandHandler();
        this.firebaseEventSubmittedCommand = new FirebaseEventSubmittedCommand();
    }

    /**
     * Process a firebase conversion event
     * @param firebaseConversionEvent conversion event
     */
    @Override
    public void accept(FirebaseConversionEvent firebaseConversionEvent) {
        this.firebaseEventSubmittedCommand.setFirebaseConversionEvent(firebaseConversionEvent);
        firebaseEventSubmittedCommandHandler.execute(firebaseEventSubmittedCommand);
    }

}
