package com.ssginc.unnie.review.debounce;

import org.springframework.context.ApplicationEvent;


public class ReviewCreatedEvent extends ApplicationEvent {
    private final long shopId;

    public ReviewCreatedEvent(Object source, long shopId) {
        super(source);
        this.shopId = shopId;
    }

    public long getShopId() {
        return shopId;
    }
}
