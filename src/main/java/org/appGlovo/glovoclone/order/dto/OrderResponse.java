package org.appGlovo.glovoclone.order.dto;

import lombok.Builder;
import lombok.Data;
import org.appGlovo.glovoclone.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private Long vendorId;
    private String vendorName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String note;
    private List<OrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}