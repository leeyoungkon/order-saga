package com.example.ordersaga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagaEvent {
    private String eventId;
    private String eventType;
    private String orderId;
    private String productId;
    private Integer quantity;
    private Integer amount;
    private String reason;

    public SagaEvent(String eventType, String orderId, String productId, Integer quantity, Integer amount, String reason) {
        this(UUID.randomUUID().toString(), eventType, orderId, productId, quantity, amount, reason);
    }
}