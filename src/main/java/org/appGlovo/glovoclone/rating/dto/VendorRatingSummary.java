package org.appGlovo.glovoclone.rating.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VendorRatingSummary {
    private Long vendorId;
    private String vendorName;
    private double averageStars;
    private int totalRatings;
    private List<RatingResponse> reviews;
}