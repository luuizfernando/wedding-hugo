package com.capisite.backend.modules.payments.enums;

public enum PaymentStatus {
    
    PENDING("pending"),
    PAID("paid"),
    REJECTED("rejected");
    
    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}