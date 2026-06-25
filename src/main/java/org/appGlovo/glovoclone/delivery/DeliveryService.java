package org.appGlovo.glovoclone.delivery;

import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.delivery.dto.AvailableOrderResponse;
import org.appGlovo.glovoclone.delivery.dto.DeliveryResponse;
import org.appGlovo.glovoclone.order.Order;
import org.appGlovo.glovoclone.order.OrderRepository;
import org.appGlovo.glovoclone.order.OrderStatus;
import org.appGlovo.glovoclone.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    // courier sees all orders waiting for pickup
    public List<AvailableOrderResponse> getAvailableOrders() {
        return orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.READY_FOR_PICKUP)
                .stream()
                .filter(order -> !deliveryRepository.existsByOrderId(order.getId()))
                .map(this::toAvailableResponse)
                .toList();
    }

    // courier claims an order
    @Transactional
    public DeliveryResponse claimOrder(Long orderId, User courier) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            throw new IllegalArgumentException("Order is not ready for pickup");
        }

        if (deliveryRepository.existsByOrderId(orderId)) {
            throw new IllegalArgumentException("Order has already been claimed by another courier");
        }

        Delivery delivery = Delivery.builder()
                .order(order)
                .courier(courier)
                .status(DeliveryStatus.ASSIGNED)
                .build();

        // advance order status
        order.setStatus(OrderStatus.PICKED_UP);
        orderRepository.save(order);
        deliveryRepository.save(delivery);

        return toResponse(delivery);
    }

    // courier updates delivery progress
    @Transactional
    public DeliveryResponse updateDeliveryStatus(Long deliveryId, DeliveryStatus newStatus, User courier) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("Delivery not found"));

        if (!delivery.getCourier().getId().equals(courier.getId())) {
            throw new IllegalArgumentException("This delivery is not assigned to you");
        }

        validateDeliveryTransition(delivery.getStatus(), newStatus);

        delivery.setStatus(newStatus);

        // sync timestamps and order status
        Order order = delivery.getOrder();
        switch (newStatus) {
            case PICKED_UP -> {
                delivery.setPickedUpAt(LocalDateTime.now());
                order.setStatus(OrderStatus.PICKED_UP);
            }
            case DELIVERING -> order.setStatus(OrderStatus.DELIVERING);
            case DELIVERED -> {
                delivery.setDeliveredAt(LocalDateTime.now());
                order.setStatus(OrderStatus.DELIVERED);
            }
            default -> {}
        }

        orderRepository.save(order);
        deliveryRepository.save(delivery);

        return toResponse(delivery);
    }

    // courier views their own delivery history
    public List<DeliveryResponse> getMyCourieries(User courier) {
        return deliveryRepository.findByCourierIdOrderByAssignedAtDesc(courier.getId())
                .stream().map(this::toResponse).toList();
    }

    private void validateDeliveryTransition(DeliveryStatus current, DeliveryStatus next) {
        boolean valid = switch (current) {
            case ASSIGNED -> next == DeliveryStatus.PICKED_UP;
            case PICKED_UP -> next == DeliveryStatus.DELIVERING;
            case DELIVERING -> next == DeliveryStatus.DELIVERED;
            default -> false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid delivery transition: " + current + " → " + next);
        }
    }

    private AvailableOrderResponse toAvailableResponse(Order order) {
        return AvailableOrderResponse.builder()
                .orderId(order.getId())
                .vendorName(order.getVendor().getName())
                .vendorAddress(order.getVendor().getAddress())
                .deliveryAddress(order.getDeliveryAddress())
                .totalAmount(order.getTotalAmount())
                .itemCount(order.getItems().size())
                .build();
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .deliveryId(delivery.getId())
                .orderId(delivery.getOrder().getId())
                .courierName(delivery.getCourier().getFullName())
                .customerName(delivery.getOrder().getCustomer().getFullName())
                .deliveryAddress(delivery.getOrder().getDeliveryAddress())
                .status(delivery.getStatus())
                .assignedAt(delivery.getAssignedAt())
                .pickedUpAt(delivery.getPickedUpAt())
                .deliveredAt(delivery.getDeliveredAt())
                .build();
    }
}