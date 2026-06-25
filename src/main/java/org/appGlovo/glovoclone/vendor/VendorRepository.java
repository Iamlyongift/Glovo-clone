package org.appGlovo.glovoclone.vendor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByOwnerId(Long ownerId);
    boolean existsByOwnerId(Long ownerId);
    List<Vendor> findByIsOpenTrue();
}