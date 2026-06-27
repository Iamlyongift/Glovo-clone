package org.appGlovo.glovoclone.admin;

import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.admin.dto.OrderSummary;
import org.appGlovo.glovoclone.admin.dto.UserSummary;
import org.appGlovo.glovoclone.admin.dto.VendorSummary;
import org.appGlovo.glovoclone.order.OrderRepository;
import org.appGlovo.glovoclone.user.User;
import org.appGlovo.glovoclone.user.UserRepository;
import org.appGlovo.glovoclone.vendor.Vendor;
import org.appGlovo.glovoclone.vendor.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final OrderRepository orderRepository;

    public List<UserSummary> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserSummary)
                .toList();
    }

    public List<VendorSummary> getAllVendors() {
        return vendorRepository.findAll()
                .stream()
                .map(this::toVendorSummary)
                .toList();
    }

    public List<OrderSummary> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toOrderSummary)
                .toList();
    }

    public UserSummary toggleUserActive(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setActive(!user.isActive());
        userRepository.save(user);

        return toUserSummary(user);
    }

    public VendorSummary toggleVendorOpen(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        vendor.setOpen(!vendor.isOpen());
        vendorRepository.save(vendor);

        return toVendorSummary(vendor);
    }

    // --- mappers ---

    private UserSummary toUserSummary(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private VendorSummary toVendorSummary(Vendor vendor) {
        return VendorSummary.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .address(vendor.getAddress())
                .cuisineType(vendor.getCuisineType())
                .isOpen(vendor.isOpen())
                .ownerEmail(vendor.getOwner().getEmail())
                .menuItemCount(vendor.getMenuItems().size())
                .createdAt(vendor.getCreatedAt())
                .build();
    }

    private OrderSummary toOrderSummary(org.appGlovo.glovoclone.order.Order order) {
        return OrderSummary.builder()
                .id(order.getId())
                .customerEmail(order.getCustomer().getEmail())
                .vendorName(order.getVendor().getName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .createdAt(order.getCreatedAt())
                .build();
    }
}