package org.appGlovo.glovoclone.notification;

import lombok.extern.slf4j.Slf4j;
import org.appGlovo.glovoclone.order.Order;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationListener {

    @EventListener
    @Async
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        String customerEmail = order.getCustomer().getEmail();
        String customerName = order.getCustomer().getFullName();

        String message = buildMessage(order, event);

        // --- Stub: log for now, swap for real email/SMS/push later ---
        log.info("📧 NOTIFICATION → [{}] ({}): {}", customerName, customerEmail, message);
    }

    private String buildMessage(Order order, OrderStatusChangedEvent event) {
        return switch (event.getNewStatus()) {
            case ACCEPTED -> String.format(
                    "Good news! Your order #%d from %s has been accepted and will be prepared shortly.",
                    order.getId(), order.getVendor().getName());
            case PREPARING -> String.format(
                    "Your order #%d is now being prepared by %s. Hang tight!",
                    order.getId(), order.getVendor().getName());
            case READY_FOR_PICKUP -> String.format(
                    "Your order #%d is ready! A courier will pick it up soon.",
                    order.getId());
            case PICKED_UP -> String.format(
                    "A courier has picked up your order #%d and is on the way!",
                    order.getId());
            case DELIVERING -> String.format(
                    "Your order #%d is on the way to %s. Almost there!",
                    order.getId(), order.getDeliveryAddress());
            case DELIVERED -> String.format(
                    "Your order #%d has been delivered. Enjoy your meal! 🍽️",
                    order.getId());
            case CANCELLED -> String.format(
                    "Your order #%d has been cancelled. Contact support if this was unexpected.",
                    order.getId());
            default -> String.format("Your order #%d status has been updated to %s.",
                    order.getId(), event.getNewStatus());
        };
    }
}