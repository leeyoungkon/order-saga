package com.example.ordersaga;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "saga_event_logs")
public class SagaEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;
    @Column(unique = true)
    private String eventId;
    private String eventType;
    private String orderId;
    private String productId;
    private Integer quantity;
    private Integer amount;
    private String reason;
    private LocalDateTime createdAt;

    public static SagaEventLog of(String source, SagaEvent event) {
        String eventId = event.getEventId();
        if (isBlank(eventId)) {
            eventId = UUID.randomUUID().toString();
            event.setEventId(eventId);
        }

        return new SagaEventLog(
                null,
                source,
                eventId,
                event.getEventType(),
                event.getOrderId(),
                event.getProductId(),
                event.getQuantity(),
                event.getAmount(),
                event.getReason(),
                LocalDateTime.now()
        );
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
