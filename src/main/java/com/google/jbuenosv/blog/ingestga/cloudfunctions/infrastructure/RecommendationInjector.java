package com.google.jbuenosv.blog.ingestga.cloudfunctions.infrastructure;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.api.services.retail.v2.CloudRetailScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.retail.v2.*;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.exception.FirebaseProcessorException;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.domain.RecommendationEvent;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.infrastructure.util.ConfigLoader;
import org.threeten.bp.Duration;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jbuenosv@google.com
 */
public class RecommendationInjector {

    public static final Logger logger = Logger.getLogger(RecommendationInjector.class.getName());

    GoogleCredentials googleCredentials;
    UserEventServiceClient userEventServiceClient;

    public RecommendationInjector() {
    }

    /**
     * Injects a RecAI User Event
     * @param conversionEvent Firebase conversion event
     */
    public void userEvent(RecommendationEvent conversionEvent) throws RuntimeException {
        try {
            String recommendationAiURI = ConfigLoader.getInstance().getEnv(ConfigLoader.getInstance().getProperty("apps.conversion.events.processor.recai.api.uri"));

            logger.info("Ready to send the RecAI event [" + conversionEvent.getEventType().name() + "].");

            googleCredentials = authorizeFromGoogleCredentials();

            UserEventServiceSettings.Builder userEventServiceSettingsBuilder = UserEventServiceSettings.newBuilder();

            userEventServiceSettingsBuilder
                    .writeUserEventSettings()
                    .setRetrySettings(
                            userEventServiceSettingsBuilder
                                    .writeUserEventSettings()
                                    .getRetrySettings()
                                    .toBuilder()
                                    .setTotalTimeout(Duration.ofSeconds(30))
                                    .build());

            UserEventServiceSettings userEventServiceSettingsEndPoint = userEventServiceSettingsBuilder
                    .setCredentialsProvider(FixedCredentialsProvider.create(googleCredentials))
                    .build();

            logger.info("Ready to create a UserEventServiceClient using userEventServiceSettingsEndPoint [" + userEventServiceSettingsEndPoint.toString() + "]");
            userEventServiceClient = UserEventServiceClient.create(userEventServiceSettingsEndPoint);
            logger.info("userEventServiceClient ready.");

            logger.info("writeUserEvent [" + conversionEvent.getUserEvent().toString() + "] to be sent.");
            WriteUserEventRequest writeUserEventRequest =
                    WriteUserEventRequest.newBuilder()
                            .setParent(recommendationAiURI)
                            .setUserEvent(conversionEvent.getUserEvent())
                            .build();
            logger.info("writeUserEventRequest [" + writeUserEventRequest + "] ready.");

            userEventServiceClient.writeUserEvent(writeUserEventRequest);
            logger.info("writeUserEventRequest has been launched.");
        }
        catch(ApiException apiException) {
            if (apiException.getMessage() != null &&  apiException.getCause() != null) {
                logger.log(Level.SEVERE,"Unable to send the RecAI event due to [" + apiException.getMessage() + "]",apiException.getCause());
            }
            else {
                logger.log(Level.SEVERE,"Unable to send the RecAI event due to [" + apiException.getMessage() + "].");
            }
        }
        catch(Exception exception) {
            if (exception.getMessage() != null &&  exception.getCause() != null) {
                logger.log(Level.SEVERE, "Unable to send the RecAI event due to [" + exception.getMessage() + "]", exception.getCause());
            }
            else {
                logger.log(Level.SEVERE, "Unable to send the RecAI event due to [" + exception.getMessage() + "].");
            }
            throw new FirebaseProcessorException(exception);
        }
        finally {
            closeAndShutdown();
        }
    }

    /**
     * Gets the credentials using Google Credentials
     * @return Google credentials
     * @throws Exception exception
     */
    private GoogleCredentials authorizeFromGoogleCredentials() throws Exception {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(getClass().getClassLoader().getResourceAsStream("recai-credentials.json"))
                .createScoped(CloudRetailScopes.all());
        logger.info("GoogleCredentials ready having scopes [" + CloudRetailScopes.all() + "].");
        credentials.refreshIfExpired();
        logger.info("GoogleCredentials having scopes [" + CloudRetailScopes.all()+ "] have been refreshed.");
        return credentials;
    }

    /**
     * Closes the user service client
     * @throws Exception exception
     */
    private void closeAndShutdown() {
        if (userEventServiceClient != null) {
            userEventServiceClient.close();
            logger.info("userEventServiceClient has been closed.");
        }
        else {
            logger.severe("userEventServiceClient is null. It can not be closed.");
        }
    }

}
