package org.appGlovo.glovoclone.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VendorSummary {
    private Long id;
    private String name;
    private String address;
    private String cuisineType;
    private boolean isOpen;
    private String ownerEmail;
    private int menuItemCount;
    private LocalDateTime createdAt;
}