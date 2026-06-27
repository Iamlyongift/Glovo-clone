package org.appGlovo.glovoclone.notification;

import lombok.Getter;
import org.appGlovo.glovoclone.order.Order;
import org.appGlovo.glovoclone.order.OrderStatus;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderStatusChangedEvent extends ApplicationEvent {

    private final Order order;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;

    public OrderStatusChangedEvent(Object source, Order order,
                                   OrderStatus previousStatus, OrderStatus newStatus) {
        super(source);
        this.order = order;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
    }
}