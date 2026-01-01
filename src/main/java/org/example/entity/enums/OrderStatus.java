package org.example.entity.enums;

public enum OrderStatus {
    NEW, CONFIRMED, PROCESSING, SHIPPED, COMPLETED, CANCELED;

    public boolean isEditable() {
        return this == NEW;
    }
}