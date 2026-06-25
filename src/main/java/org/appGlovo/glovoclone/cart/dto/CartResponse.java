package org.appGlovo.glovoclone.cart.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long cartId;
    private Long vendorId;
    private String vendorName;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
}