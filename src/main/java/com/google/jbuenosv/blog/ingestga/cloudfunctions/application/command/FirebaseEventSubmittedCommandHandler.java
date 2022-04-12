package com.google.jbuenosv.blog.ingestga.cloudfunctions.application.command;

import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.exception.FirebaseProcessorException;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.domain.FirebaseConversionEvent;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.domain.RecommendationEvent;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.infrastructure.RecommendationInjector;

import java.util.logging.Logger;

/**
 * Created by jbuenosv@google.com
 */
public class FirebaseEventSubmittedCommandHandler implements  CommandHandler {

    public static final Logger logger = Logger.getLogger(FirebaseEventSubmittedCommandHandler.class.getName());

    private RecommendationInjector recommendationInjector;

    /**
     * Default constructor
     */
    public FirebaseEventSubmittedCommandHandler() {
        recommendationInjector = new RecommendationInjector();
    }

    /**
     * Executes the command
     * @param command command to be executed
     */
    @Override
    public void execute(Command command) {
        FirebaseEventSubmittedCommand firebaseEventSubmittedCommand = (FirebaseEventSubmittedCommand)command;
        FirebaseConversionEvent firebaseConversionEvent = firebaseEventSubmittedCommand.getFirebaseConversionEvent();

        try {
            logger.info("Process the firebase event [" + firebaseConversionEvent.getEventName()  + "] start.");
            recommendationInjector.userEvent(new RecommendationEvent.RecommendationEventBuilder(firebaseConversionEvent).build());
            logger.info("Process the firebase event [" + firebaseConversionEvent.getEventName()  + "] ends.");
        }
        catch(Exception e) {
            logger.severe("Unable to process the firebase event [" + firebaseConversionEvent.getEventName() + "]");
            throw new FirebaseProcessorException(e);
        }

    }

}
