package org.appGlovo.glovoclone.cart;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.cart.dto.*;
import org.appGlovo.glovoclone.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(cartService.getCart(customer));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody AddCartItemRequest request,
            @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(cartService.addItem(request, customer));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(cartService.updateItemQuantity(cartItemId, request, customer));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(cartService.removeItem(cartItemId, customer));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal User customer) {
        cartService.clearCart(customer);
        return ResponseEntity.noContent().build();
    }
}