package org.appGlovo.glovoclone.cart;

import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.cart.dto.*;
import org.appGlovo.glovoclone.user.User;
import org.appGlovo.glovoclone.vendor.MenuItem;
import org.appGlovo.glovoclone.vendor.MenuItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;

    public CartResponse addItem(AddCartItemRequest request, User customer) {
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> cartRepository.save(
                        Cart.builder().customer(customer).build()));

        if (cart.getVendor() == null) {
            cart.setVendor(menuItem.getVendor());
        } else if (!cart.getVendor().getId().equals(menuItem.getVendor().getId())) {
            throw new IllegalArgumentException(
                    "Your cart already has items from another vendor. Clear your cart first.");
        }

        // if item already in cart, just increase quantity
        CartItem existing = cart.getItems().stream()
                .filter(ci -> ci.getMenuItem().getId().equals(menuItem.getId()))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItemRepository.save(existing);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }

        cartRepository.save(cart);
        return toResponse(cart);
    }

    public CartResponse getCart(User customer) {
        Cart cart = getOrCreateCart(customer);
        return toResponse(cart);
    }

    public CartResponse updateItemQuantity(Long cartItemId, UpdateCartItemRequest request, User customer) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        validateOwnership(item, customer);
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return toResponse(item.getCart());
    }

    public CartResponse removeItem(Long cartItemId, User customer) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        validateOwnership(item, customer);
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        if (cart.getItems().isEmpty()) {
            cart.setVendor(null);
        }
        cartRepository.save(cart);

        return toResponse(cart);
    }

    public void clearCart(User customer) {
        Cart cart = getOrCreateCart(customer);
        cart.getItems().clear();
        cart.setVendor(null);
        cartRepository.save(cart);
    }

    // --- helpers ---

    public Cart getOrCreateCart(User customer) {
        return cartRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().customer(customer).build()));
    }

    private void validateOwnership(CartItem item, User customer) {
        if (!item.getCart().getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("This cart item does not belong to you");
        }
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(ci -> CartItemResponse.builder()
                        .cartItemId(ci.getId())
                        .menuItemId(ci.getMenuItem().getId())
                        .name(ci.getMenuItem().getName())
                        .price(ci.getMenuItem().getPrice())
                        .quantity(ci.getQuantity())
                        .subtotal(ci.getMenuItem().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                        .build())
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .vendorId(cart.getVendor() != null ? cart.getVendor().getId() : null)
                .vendorName(cart.getVendor() != null ? cart.getVendor().getName() : null)
                .items(itemResponses)
                .totalAmount(total)
                .build();
    }
}