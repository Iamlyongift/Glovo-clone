package org.appGlovo.glovoclone.rating;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.rating.dto.CreateRatingRequest;
import org.appGlovo.glovoclone.rating.dto.RatingResponse;
import org.appGlovo.glovoclone.rating.dto.VendorRatingSummary;
import org.appGlovo.glovoclone.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    // customer submits a rating after delivery
    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(
            @Valid @RequestBody CreateRatingRequest request,
            @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(ratingService.submitRating(request, customer));
    }

    // anyone can view a vendor's ratings and average score
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<VendorRatingSummary> getVendorRatings(@PathVariable Long vendorId) {
        return ResponseEntity.ok(ratingService.getVendorRatings(vendorId));
    }

    // customer views their own submitted ratings
    @GetMapping("/my-ratings")
    public ResponseEntity<List<RatingResponse>> getMyRatings(
            @AuthenticationPrincipal User customer) {
        return ResponseEntity.ok(ratingService.getMyRatings(customer));
    }
}