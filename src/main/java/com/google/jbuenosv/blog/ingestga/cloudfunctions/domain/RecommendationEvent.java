package com.google.jbuenosv.blog.ingestga.cloudfunctions.domain;

import com.fasterxml.jackson.databind.JsonNode;

import com.google.cloud.retail.v2.*;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.exception.FirebaseProcessorException;
import com.google.protobuf.Timestamp;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by jbuenosv@google.com
 */
public class RecommendationEvent implements Serializable {

    public static final Logger logger = Logger.getLogger(RecommendationEvent.class.getName());

    private RecommendationEventType eventType;
    private UserEvent userEvent;

    public RecommendationEventType getEventType() {
        return eventType;
    }

    private void setEventType(RecommendationEventType eventType) {
        this.eventType = eventType;
    }

    public UserEvent getUserEvent() {
        return userEvent;
    }

    private void setUserEvent(UserEvent userEvent) {
        this.userEvent = userEvent;
    }

    public static class RecommendationEventBuilder {

        private FirebaseConversionEvent conversionEvent;

        public RecommendationEventBuilder(FirebaseConversionEvent conversionEvent) {
            this.conversionEvent = conversionEvent;
        }

        public RecommendationEvent build() {
            RecommendationEvent recommendationEvent;

            try {
                logger.info("RecommendationEvent build start.");
                FirebaseConversionEventType firebaseConversionEventType = FirebaseConversionEventType.fromString(conversionEvent.getEventName());
                recommendationEvent = detailPageViewEventBuilder();
                logger.info("RecommendationEvent build ends.");
            }
            catch (Exception e) {
                logger.severe("Unable to build the recommendation AI event due to [" + e.getMessage() + "].");
                throw new FirebaseProcessorException(e);
            }

            return recommendationEvent;
        }

        /**
         * Builds a Recommendation AI detail page view user event
         * @return Recommendation AI detail page view user event
         */
        private RecommendationEvent detailPageViewEventBuilder() {
            JsonNode rootNode;
            RecommendationEvent recommendationEvent = new RecommendationEvent();
            UserEvent userEvent;
            UserEvent.Builder userEventBuilder = UserEvent.newBuilder();

            recommendationEvent.setEventType(RecommendationEventType.DETAIL_PAGE_VIEW);
            logger.info("RecommendationEvent type [" + RecommendationEventType.DETAIL_PAGE_VIEW + "] to be built.");

            try {
                userEventBuilder.setVisitorId(this.conversionEvent.getUserPseudoId());
                userEventBuilder.setEventTime(getUserEventCurrentTime());
                String item_id = conversionEvent.getEventJsonRootNode().path("eventDim").get(0).path("params").path("item_id").path("stringValue").textValue();

                if (item_id != null) {
                    ProductDetail.Builder productDetailBuilder = ProductDetail.newBuilder();
                    productDetailBuilder.setProduct(Product.newBuilder().setId(item_id).build());
                    userEventBuilder.addProductDetails(productDetailBuilder.build());
                }
                else {
                    logger.severe("[" + conversionEvent.getFirebaseConversionPlatformSource().toString() + "] - Firebase item_id is null.");
                    throw new FirebaseProcessorException("Firebase item_id is null.");
                }

                userEventBuilder.setEventType(RecommendationEventType.DETAIL_PAGE_VIEW.getEventName());
                userEventBuilder
                        .setUserInfo(
                                UserInfo.newBuilder()
                                        .setUserId(this.conversionEvent.getUserId())
                                        .build());
                userEvent = userEventBuilder.build();
                logger.info("userEvent [" +  userEvent + "].");
            }
            catch(Exception e) {
                logger.severe("Unable to build the [" + RecommendationEventType.DETAIL_PAGE_VIEW + "] due to [" + e.getMessage() + "]");
                throw new FirebaseProcessorException(e);
            }

            recommendationEvent.setUserEvent(userEvent);

            logger.info("RecommendationEvent type [" + RecommendationEventType.DETAIL_PAGE_VIEW + "] has built.");

            return recommendationEvent;
        }

        /**
         * Gets the current time in Proto format
         * @return Current time
         */
        private Timestamp getUserEventCurrentTime() {
            long millis = System.currentTimeMillis();
            return Timestamp.newBuilder()
                    .setSeconds(millis / 1000)
                    .setNanos((int) ((millis % 1000) * 1000000))
                    .build();
        }

    }

    private RecommendationEvent() {
    }

}
