package org.appGlovo.glovoclone.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    boolean existsByOrderId(Long orderId);
    List<Rating> findByVendorIdOrderByCreatedAtDesc(Long vendorId);
    Optional<Rating> findByOrderIdAndCustomerId(Long orderId, Long customerId);

    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.vendor.id = :vendorId")
    Double findAverageStarsByVendorId(Long vendorId);
}