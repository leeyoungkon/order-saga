package com.example.ordersaga;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaConsumer {

    private final OrderSagaService orderSagaService;

    @KafkaListener(topics = "stock-events", groupId = "order-service-group")
    public void consumeStockEvent(SagaEvent event) {
        event.setEventType(normalizeEventType(event.getEventType()));
        if (orderSagaService.isDuplicateIncomingEvent("STOCK_SERVICE", event)) {
            log.info("[ORDER] duplicate stock event ignored. eventId={}, orderId={}", event.getEventId(), event.getOrderId());
            return;
        }
        log.info("[ORDER] stock event received = {}", event);
        orderSagaService.logIncomingEvent("STOCK_SERVICE", event);

        switch (event.getEventType()) {
            case "StockReserved" -> orderSagaService.markStockReserved(event.getOrderId());
            case "StockReleased" -> orderSagaService.cancelOrder(event.getOrderId(), event.getReason());
        }
    }

    @KafkaListener(topics = "payment-events", groupId = "order-service-group")
    public void consumePaymentEvent(SagaEvent event) {
        event.setEventType(normalizeEventType(event.getEventType()));
        if (orderSagaService.isDuplicateIncomingEvent("PAYMENT_SERVICE", event)) {
            log.info("[ORDER] duplicate payment event ignored. eventId={}, orderId={}", event.getEventId(), event.getOrderId());
            return;
        }
        log.info("[ORDER] payment event received = {}", event);
        orderSagaService.logIncomingEvent("PAYMENT_SERVICE", event);

        switch (event.getEventType()) {
            case "PaymentCompleted" -> orderSagaService.markCompleted(event.getOrderId());
            case "PaymentFailed" -> orderSagaService.cancelOrder(event.getOrderId(), event.getReason());
        }
    }

    private String normalizeEventType(String eventType) {
        if (eventType == null) {
            return "";
        }
        String normalized = eventType.trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "stockreserved" -> "StockReserved";
            case "stockreleased" -> "StockReleased";
            case "paymentcompleted" -> "PaymentCompleted";
            case "paymentfailed" -> "PaymentFailed";
            default -> eventType;
        };
    }
}