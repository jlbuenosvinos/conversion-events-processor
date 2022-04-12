package com.google.jbuenosv.blog.ingestga.cloudfunctions.domain;

import java.util.Arrays;

/**
 * Created by jbuenosv@google.com
 */
public enum RecommendationEventType {

    ADD_TO_CART("add-to-cart"),
    DETAIL_PAGE_VIEW("detail-page-view"),
    HOME_PAGE_VIEW("home-page-view"),
    PURCHASE_COMPLETE("purchase-complete");

    private final String eventName;

    RecommendationEventType(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    @SuppressWarnings("unused")
    public static RecommendationEventType fromString(String fromName) throws IllegalArgumentException {
        return Arrays.stream(RecommendationEventType.values())
                .filter(v -> v.eventName.equals(fromName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
