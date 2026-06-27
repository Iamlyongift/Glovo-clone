package org.appGlovo.glovoclone.admin.dto;

import lombok.Builder;
import lombok.Data;
import org.appGlovo.glovoclone.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderSummary {
    private Long id;
    private String customerEmail;
    private String vendorName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private LocalDateTime createdAt;
}