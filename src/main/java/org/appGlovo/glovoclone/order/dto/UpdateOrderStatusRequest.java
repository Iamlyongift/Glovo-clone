package org.appGlovo.glovoclone.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.appGlovo.glovoclone.order.OrderStatus;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}