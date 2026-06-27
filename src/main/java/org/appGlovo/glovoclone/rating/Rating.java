package org.appGlovo.glovoclone.rating;

import jakarta.persistence.*;
import lombok.*;
import org.appGlovo.glovoclone.order.Order;
import org.appGlovo.glovoclone.user.User;
import org.appGlovo.glovoclone.vendor.Vendor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order; // one rating per order — prevents duplicate reviews

    @Column(nullable = false)
    private int stars; // 1 to 5

    private String review; // optional written review

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}