package org.appGlovo.glovoclone.vendor;

import jakarta.persistence.*;
import lombok.*;
import org.appGlovo.glovoclone.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String address;

    private String cuisineType; // e.g. "Local", "Fast Food", "Continental"

    private String logoUrl;

    @Builder.Default
    private boolean isOpen = true;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner; // must have role VENDOR

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuItem> menuItems = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}