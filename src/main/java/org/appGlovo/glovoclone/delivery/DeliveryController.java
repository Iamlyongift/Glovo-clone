package org.appGlovo.glovoclone.delivery;

import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.delivery.dto.AvailableOrderResponse;
import org.appGlovo.glovoclone.delivery.dto.DeliveryResponse;
import org.appGlovo.glovoclone.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // courier sees available orders to claim
    @GetMapping("/available")
    public ResponseEntity<List<AvailableOrderResponse>> getAvailableOrders() {
        return ResponseEntity.ok(deliveryService.getAvailableOrders());
    }

    // courier claims an order
    @PostMapping("/claim/{orderId}")
    public ResponseEntity<DeliveryResponse> claimOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal User courier) {
        return ResponseEntity.ok(deliveryService.claimOrder(orderId, courier));
    }

    // courier updates their delivery status
    @PatchMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long deliveryId,
            @RequestParam DeliveryStatus status,
            @AuthenticationPrincipal User courier) {
        return ResponseEntity.ok(deliveryService.updateDeliveryStatus(deliveryId, status, courier));
    }

    // courier views their delivery history
    @GetMapping("/my-deliveries")
    public ResponseEntity<List<DeliveryResponse>> getMyDeliveries(
            @AuthenticationPrincipal User courier) {
        return ResponseEntity.ok(deliveryService.getMyCourieries(courier));
    }
}