package com.google.jbuenosv.blog.ingestga.cloudfunctions.domain;

import java.util.*;

/**
 * Created by jbuenosv@google.com
 */
public enum FirebaseConversionEventType {

    ADD_TO_CART("add_to_cart"),
    PURCHASE("purchase"),
    VIEW_ITEM("view_item"),
    VIEW_PROMOTION("view_promotion");

    private final String eventName;

    FirebaseConversionEventType(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    public static FirebaseConversionEventType fromString(String fromName) throws IllegalArgumentException {
        return Arrays.stream(FirebaseConversionEventType.values())
                .filter(v -> v.eventName.equals(fromName))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
