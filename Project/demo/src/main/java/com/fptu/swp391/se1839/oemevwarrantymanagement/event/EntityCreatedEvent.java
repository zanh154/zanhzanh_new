package com.fptu.swp391.se1839.oemevwarrantymanagement.event;

import org.springframework.context.ApplicationEvent;

public class EntityCreatedEvent<T> extends ApplicationEvent {

    private final T entity;

    public EntityCreatedEvent(Object source, T entity) {
        super(source);
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }
}