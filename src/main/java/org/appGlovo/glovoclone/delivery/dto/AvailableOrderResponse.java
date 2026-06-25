package org.appGlovo.glovoclone.delivery.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AvailableOrderResponse {
    private Long orderId;
    private String vendorName;
    private String vendorAddress;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private int itemCount;
}