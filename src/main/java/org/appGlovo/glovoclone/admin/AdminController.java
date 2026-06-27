package org.appGlovo.glovoclone.admin;

import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.admin.dto.OrderSummary;
import org.appGlovo.glovoclone.admin.dto.UserSummary;
import org.appGlovo.glovoclone.admin.dto.VendorSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/vendors")
    public ResponseEntity<List<VendorSummary>> getAllVendors() {
        return ResponseEntity.ok(adminService.getAllVendors());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderSummary>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @PatchMapping("/users/{userId}/toggle-active")
    public ResponseEntity<UserSummary> toggleUserActive(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.toggleUserActive(userId));
    }

    @PatchMapping("/vendors/{vendorId}/toggle-open")
    public ResponseEntity<VendorSummary> toggleVendorOpen(@PathVariable Long vendorId) {
        return ResponseEntity.ok(adminService.toggleVendorOpen(vendorId));
    }
}