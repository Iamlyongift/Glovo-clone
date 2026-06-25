package org.appGlovo.glovoclone.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateVendorRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String address;

    private String cuisineType;

    private String logoUrl;
}