package com.fptu.swp391.se1839.oemevwarrantymanagement.event;

import org.springframework.context.ApplicationEvent;

/**
 * Sự kiện dùng để thông báo khi một entity đã được cập nhật (UPDATED).
 */
public class EntityUpdatedEvent<T> extends ApplicationEvent {

    private final T entity;

    public EntityUpdatedEvent(Object source, T entity) {
        super(source);
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }
}
