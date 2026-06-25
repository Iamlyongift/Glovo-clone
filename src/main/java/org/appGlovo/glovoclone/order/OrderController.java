package org.appGlovo.glovoclone.order;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.order.dto.*;
import org.appGlovo.glovoclone.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Customer places order from their cart
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(orderService.placeOrder(request, customer));
    }

    // Customer views their order history
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(orderService.getMyOrders(customer));
    }

    // Vendor views incoming orders for their restaurant
    @GetMapping("/vendor-orders")
    public ResponseEntity<List<OrderResponse>> getVendorOrders(@AuthenticationPrincipal User vendorUser) {
        return ResponseEntity.ok(orderService.getVendorOrders(vendorUser));
    }

    // Vendor updates order status
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @AuthenticationPrincipal User vendorUser) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, request, vendorUser));
    }
}