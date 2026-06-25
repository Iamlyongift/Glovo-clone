package org.appGlovo.glovoclone.cart;

import jakarta.persistence.*;
import lombok.*;
import org.appGlovo.glovoclone.user.User;
import org.appGlovo.glovoclone.vendor.Vendor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor; // null until first item added; locks cart to one vendor

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
}