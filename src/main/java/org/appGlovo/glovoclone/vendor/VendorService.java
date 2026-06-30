package org.appGlovo.glovoclone.vendor;

import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.user.User;
import org.appGlovo.glovoclone.vendor.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final MenuItemRepository menuItemRepository;

    public VendorResponse createVendor(CreateVendorRequest request, User owner) {
        if (vendorRepository.existsByOwnerId(owner.getId())) {
            throw new IllegalArgumentException("This user already has a vendor profile");
        }

        Vendor vendor = Vendor.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .cuisineType(request.getCuisineType())
                .logoUrl(request.getLogoUrl())
                .owner(owner)
                .build();

        vendorRepository.save(vendor);
        return toResponse(vendor);
    }

    public VendorResponse getVendorByOwnerId(Long ownerId) {
        Vendor vendor = vendorRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("No vendor profile found for this user"));
        return toResponse(vendor);
    }

    public List<VendorResponse> getAllOpenVendors() {
        return vendorRepository.findByIsOpenTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public VendorResponse getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        return toResponse(vendor);
    }

    public MenuItemResponse addMenuItem(Long vendorId, MenuItemRequest request, User owner) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        if (!vendor.getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("You do not own this vendor profile");
        }

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .vendor(vendor)
                .build();

        menuItemRepository.save(item);
        return toMenuResponse(item);
    }

    private VendorResponse toResponse(Vendor vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .description(vendor.getDescription())
                .address(vendor.getAddress())
                .cuisineType(vendor.getCuisineType())
                .logoUrl(vendor.getLogoUrl())
                .isOpen(vendor.isOpen())
                .menuItems(vendor.getMenuItems().stream().map(this::toMenuResponse).toList())
                .build();
    }

    private MenuItemResponse toMenuResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .available(item.isAvailable())
                .build();
    }
}