package com.google.jbuenosv.blog.ingestga.cloudfunctions.application.process;

import com.google.jbuenosv.blog.ingestga.cloudfunctions.domain.FirebaseConversionEvent;

/**
 * Created by jbuenosv@google.com
 */
public interface FirebaseConversionEventProcessManager {

    /**
     * Process a firebase conversion event
     * @param firebaseConversionEvent conversion event
     */
    void accept(FirebaseConversionEvent firebaseConversionEvent);

}
