package org.appGlovo.glovoclone.order;

import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.cart.Cart;
import org.appGlovo.glovoclone.cart.CartService;
import org.appGlovo.glovoclone.notification.OrderStatusChangedEvent;
import org.appGlovo.glovoclone.order.dto.*;
import org.appGlovo.glovoclone.user.User;
import org.appGlovo.glovoclone.vendor.Vendor;
import org.appGlovo.glovoclone.vendor.VendorRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final VendorRepository vendorRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request, User customer) {
        Cart cart = cartService.getOrCreateCart(customer);

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Your cart is empty");
        }

        Vendor vendor = cart.getVendor();

        // build order items from cart — snapshot price at time of order
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .menuItem(cartItem.getMenuItem())
                        .quantity(cartItem.getQuantity())
                        .priceAtOrder(cartItem.getMenuItem().getPrice())
                        .build())
                .toList();

        BigDecimal total = orderItems.stream()
                .map(i -> i.getPriceAtOrder().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .customer(customer)
                .vendor(vendor)
                .status(OrderStatus.PLACED)
                .totalAmount(total)
                .deliveryAddress(request.getDeliveryAddress())
                .note(request.getNote())
                .build();

        // link items to order
        orderItems.forEach(item -> item.setOrder(order));
        order.getItems().addAll(orderItems);

        orderRepository.save(order);

        // clear the cart after successful order
        cartService.clearCart(customer);
        eventPublisher.publishEvent(
                new OrderStatusChangedEvent(this, order, null, OrderStatus.PLACED)
        );

        return toResponse(order);
    }

    public List<OrderResponse> getMyOrders(User customer) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream().map(this::toResponse).toList();
    }

    public List<OrderResponse> getVendorOrders(User vendorUser) {
        Vendor vendor = vendorRepository.findByOwnerId(vendorUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor profile not found"));
        return orderRepository.findByVendorIdOrderByCreatedAtDesc(vendor.getId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, UpdateOrderStatusRequest request, User vendorUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // make sure only the owning vendor can update status
        if (!order.getVendor().getOwner().getId().equals(vendorUser.getId())) {
            throw new IllegalArgumentException("You do not own this order's vendor");
        }

        OrderStatus previousStatus = order.getStatus(); // capture BEFORE changing it
        validateStatusTransition(previousStatus, request.getStatus());
        order.setStatus(request.getStatus());
        orderRepository.save(order);
        eventPublisher.publishEvent(
                new OrderStatusChangedEvent(this, order, previousStatus, request.getStatus())
        );
        return toResponse(order);
    }

    // enforce valid status transitions — vendor can't skip steps or go backwards
    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PLACED -> next == OrderStatus.ACCEPTED || next == OrderStatus.CANCELLED;
            case ACCEPTED -> next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;
            case PREPARING -> next == OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP -> next == OrderStatus.PICKED_UP;
            case PICKED_UP -> next == OrderStatus.DELIVERING;
            case DELIVERING -> next == OrderStatus.DELIVERED;
            default -> false; // DELIVERED and CANCELLED are terminal
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid status transition: " + current + " → " + next);
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .menuItemId(item.getMenuItem().getId())
                        .name(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .priceAtOrder(item.getPriceAtOrder())
                        .subtotal(item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .vendorId(order.getVendor().getId())
                .vendorName(order.getVendor().getName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .note(order.getNote())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}