package org.appGlovo.glovoclone.vendor.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VendorResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String cuisineType;
    private String logoUrl;
    private boolean isOpen;
    private List<MenuItemResponse> menuItems;
}