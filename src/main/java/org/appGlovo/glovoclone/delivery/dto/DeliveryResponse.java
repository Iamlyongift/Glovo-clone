package org.appGlovo.glovoclone.delivery.dto;

import lombok.Builder;
import lombok.Data;
import org.appGlovo.glovoclone.delivery.DeliveryStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryResponse {
    private Long deliveryId;
    private Long orderId;
    private String courierName;
    private String customerName;
    private String deliveryAddress;
    private DeliveryStatus status;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
}