package org.appGlovo.glovoclone.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private Long menuItemId;
    private String name;
    private int quantity;
    private BigDecimal priceAtOrder;
    private BigDecimal subtotal;
}