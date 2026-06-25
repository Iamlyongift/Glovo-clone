package org.appGlovo.glovoclone.delivery;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    boolean existsByOrderId(Long orderId);
    List<Delivery> findByCourierIdOrderByAssignedAtDesc(Long courierId);

}