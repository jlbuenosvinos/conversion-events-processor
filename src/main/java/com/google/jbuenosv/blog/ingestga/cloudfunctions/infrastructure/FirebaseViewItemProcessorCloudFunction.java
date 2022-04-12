package com.google.jbuenosv.blog.ingestga.cloudfunctions.infrastructure;

import com.google.cloud.functions.RawBackgroundFunction;
import com.google.cloud.functions.Context;

import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.exception.FirebaseProcessorException;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.process.FirebaseConversionEventProcessManager;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.process.FirebaseConversionEventProcessManagerImpl;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.domain.FirebaseConversionEvent;

import java.util.logging.Logger;

/**
 * Created by jbuenosv@google.com
 */
@SuppressWarnings("ALL")
public class FirebaseViewItemProcessorCloudFunction implements RawBackgroundFunction {

    public static final Logger logger = Logger.getLogger(FirebaseViewItemProcessorCloudFunction.class.getName());

    @Override
    public void accept(String eventJson, Context context) {
        FirebaseConversionEvent firebaseConversionEvent;

        try {
            logger.info("Accept event [" + context.eventId()  + "," + context.timestamp()  + "] start.");
            if (eventJson != null) {
                logger.info("Firebase JSON payload [" + eventJson  + "].");
                firebaseConversionEvent = new FirebaseConversionEvent.FirebaseConversionEventBuilder(eventJson).build();
                FirebaseConversionEventProcessManager firebaseConversionEventProcessManager = new FirebaseConversionEventProcessManagerImpl();
                firebaseConversionEventProcessManager.accept(firebaseConversionEvent);
                logger.info("Accept event [" + context.eventId()  + "," + context.timestamp()  + "] ends.");
            } // end if
            else {
                logger.severe("Firebase event payload is null.");
                throw new FirebaseProcessorException("Firebase event payload is null.");
            }
            logger.info("Accept event [" + context.eventId()  + "," + context.timestamp()  + "] ends.");
        }
        catch(Exception e) {
            logger.severe("Unable to process the event [" + context.eventId() + "] due to [" + e.getMessage() + "].");
            throw new FirebaseProcessorException(e);
        }

    }

}