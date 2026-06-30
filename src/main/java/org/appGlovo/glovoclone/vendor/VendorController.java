package org.appGlovo.glovoclone.vendor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.user.User;
import org.appGlovo.glovoclone.vendor.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    public ResponseEntity<VendorResponse> createVendor(
            @Valid @RequestBody CreateVendorRequest request,
            @AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(vendorService.createVendor(request, owner));
    }

    @GetMapping
    public ResponseEntity<List<VendorResponse>> getAllOpenVendors() {
        return ResponseEntity.ok(vendorService.getAllOpenVendors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorResponse> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    @PostMapping("/{id}/menu-items")
    public ResponseEntity<MenuItemResponse> addMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request,
            @AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(vendorService.addMenuItem(id, request, owner));
    }

    @GetMapping("/me")
    public ResponseEntity<VendorResponse> getMyVendor(@AuthenticationPrincipal User owner) {
        return ResponseEntity.ok(vendorService.getVendorByOwnerId(owner.getId()));
    }
}