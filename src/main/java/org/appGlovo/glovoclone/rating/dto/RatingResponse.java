package org.appGlovo.glovoclone.rating.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponse {
    private Long id;
    private String customerName;
    private Long vendorId;
    private String vendorName;
    private Long orderId;
    private int stars;
    private String review;
    private LocalDateTime createdAt;
}