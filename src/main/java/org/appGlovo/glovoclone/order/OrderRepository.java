package org.appGlovo.glovoclone.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Order> findByVendorIdOrderByCreatedAtDesc(Long vendorId);
    List<Order> findByStatusOrderByCreatedAtAsc(OrderStatus status);
}