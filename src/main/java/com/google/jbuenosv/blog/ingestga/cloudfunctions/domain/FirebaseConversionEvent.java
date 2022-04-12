package com.google.jbuenosv.blog.ingestga.cloudfunctions.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.exception.FirebaseProcessorException;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

/**
 * Created by jbuenosv@google.com
 */
public class FirebaseConversionEvent implements java.io.Serializable {

    public static final Logger logger = Logger.getLogger(FirebaseConversionEvent.class.getName());

    private String eventJson;
    private String eventName;
    private GregorianCalendar eventDate;
    private JsonNode eventJsonRootNode;
    private String userId;
    private String userPseudoId;

    private FirebaseConversionPlatformSource firebaseConversionPlatformSource;

    public String getEventName() {
        return eventName;
    }

    public GregorianCalendar getEventDate() {
        return eventDate;
    }

    private void setEventDate(GregorianCalendar eventDate) {
        this.eventDate = eventDate;
    }

    private void setEventName(String eventName) {
        this.eventName = eventName;
    }

    private void setEventJsonRootNode(JsonNode eventJsonRootNode) {
        this.eventJsonRootNode = eventJsonRootNode;
    }

    public String getUserId() {
        return userId;
    }

    private void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPseudoId() {
        return userPseudoId;
    }

    private void setUserPseudoId(String userPseudoId) {
        this.userPseudoId = userPseudoId;
    }

    public JsonNode getEventJsonRootNode() {
        return eventJsonRootNode;
    }

    public FirebaseConversionPlatformSource getFirebaseConversionPlatformSource() {
        return firebaseConversionPlatformSource;
    }

    private void setFirebaseConversionPlatformSource(FirebaseConversionPlatformSource firebaseConversionPlatformSource) {
        this.firebaseConversionPlatformSource = firebaseConversionPlatformSource;
    }

    public static class FirebaseConversionEventBuilder {
        private String eventJson;
        private String eventBuilderName;

        public FirebaseConversionEventBuilder(String eventJson) {
            this.eventJson = eventJson;
        }

        public FirebaseConversionEvent build() {
            FirebaseConversionEvent fireBaseConversionEvent =  new FirebaseConversionEvent();
            GregorianCalendar eventDate = new GregorianCalendar();
            JsonNode eventDimNode;
            JsonNode userDimNode,userPseudoIdNode;
            JsonNode userDimUserIdNode;
            JsonNode rootNode;
            JsonNode userDimStoreCountryNode;

            try {
                ObjectMapper mapper = new ObjectMapper();
                rootNode = mapper.readTree(this.eventJson);
                fireBaseConversionEvent.setEventJsonRootNode(rootNode);

                fireBaseConversionEvent
                        .setFirebaseConversionPlatformSource(new FirebaseConversionPlatformSource.FirebaseConversionPlatformSourceBuilder(rootNode)
                                .build());

                eventDimNode = rootNode.path("eventDim");
                userDimNode = rootNode.path("userDim");

                if (userDimNode != null) {
                    userDimUserIdNode = userDimNode.path("userId");

                    if (userDimUserIdNode != null) {
                        if (userDimUserIdNode.textValue() != null) {
                            fireBaseConversionEvent.setUserId(userDimUserIdNode.textValue());
                        }
                        else {
                            logger.severe("[" + fireBaseConversionEvent.getFirebaseConversionPlatformSource().toString() + "] - Firebase user id is null.");
                            throw new FirebaseProcessorException("Firebase user id is null.");
                        }
                    }
                    else {
                        logger.severe("[" + fireBaseConversionEvent.getFirebaseConversionPlatformSource().toString() + "] - Firebase user id is null.");
                        throw new FirebaseProcessorException("Firebase user id is null.");
                    }

                    // user_pseudo_id
                    userPseudoIdNode = userDimNode.path("userProperties").path("user_pseudo_id").path("value").path("stringValue");

                    if (userPseudoIdNode != null) {
                        if (userPseudoIdNode.textValue() != null) {
                            fireBaseConversionEvent.setUserPseudoId(userPseudoIdNode.textValue());
                        }
                        else {
                            logger.severe("[" + fireBaseConversionEvent.getFirebaseConversionPlatformSource().toString() + "] - Firebase user id is null.");
                            throw new FirebaseProcessorException("Firebase user pseudo id is null.");
                        }
                    }
                    else {
                        logger.severe("[" + fireBaseConversionEvent.getFirebaseConversionPlatformSource().toString() + "] - Firebase user id is null.");
                        throw new FirebaseProcessorException("Firebase user pseudo id is null.");
                    }

                }
                else {
                    logger.severe("Firebase user dimension is null.");
                    throw new FirebaseProcessorException("Unable to find the Firebase user dimension.");
                }

                if (eventDimNode != null) {
                    if (eventDimNode.isArray()) {
                        if (eventDimNode.size() != 0) {
                            eventBuilderName = eventDimNode.get(0).path("name").textValue();
                            eventDate.setTime(new SimpleDateFormat("yyyyMMdd").parse(eventDimNode.get(0).path("date") .textValue()));
                            logger.info("Firebase incoming event type and date [" + eventBuilderName + "," +  eventDate.toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()  + "].");
                            fireBaseConversionEvent.setEventName(eventBuilderName);
                            fireBaseConversionEvent.setEventDate(eventDate);
                        }
                        else {
                            logger.severe("Firebase event dimension is an empty array.");
                            throw new FirebaseProcessorException("Unable to find the Firebase event dimension.");
                        }
                    }
                    else {
                        logger.severe("Firebase event dimension is not an array.");
                        throw new FirebaseProcessorException("Unable to find the Firebase event dimension.");
                    }
                }
                else {
                    logger.severe("Firebase event dimension is null.");
                    throw new FirebaseProcessorException("Unable to find the Firebase event dimension.");
                }

                logger.info("FirebaseConversionEvent has been built.");
            }
            catch(Exception e) {
                if (e.getCause() != null && e.getCause().getStackTrace() != null) {
                    logger.severe("Unable to parse the Firebase event due to [" + e.getCause().getStackTrace().toString() + "].");
                }
                else {
                    logger.severe("Unable to parse the Firebase event.");
                }
                throw new FirebaseProcessorException(e);
            }

            return fireBaseConversionEvent;
        }

    }

    private FirebaseConversionEvent() {
    }

}
