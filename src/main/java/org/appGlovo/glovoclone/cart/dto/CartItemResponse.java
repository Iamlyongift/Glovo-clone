package org.appGlovo.glovoclone.cart.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private Long menuItemId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subtotal;
}