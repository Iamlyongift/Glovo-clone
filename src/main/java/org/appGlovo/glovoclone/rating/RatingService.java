package org.appGlovo.glovoclone.rating;

import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.order.Order;
import org.appGlovo.glovoclone.order.OrderRepository;
import org.appGlovo.glovoclone.order.OrderStatus;
import org.appGlovo.glovoclone.rating.dto.CreateRatingRequest;
import org.appGlovo.glovoclone.rating.dto.RatingResponse;
import org.appGlovo.glovoclone.rating.dto.VendorRatingSummary;
import org.appGlovo.glovoclone.user.User;
import org.appGlovo.glovoclone.vendor.Vendor;
import org.appGlovo.glovoclone.vendor.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final VendorRepository vendorRepository;

    public RatingResponse submitRating(CreateRatingRequest request, User customer) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // only the customer who placed the order can rate it
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("You can only rate your own orders");
        }

        // order must be delivered before rating
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new IllegalArgumentException(
                    "You can only rate an order after it has been delivered");
        }

        // one rating per order
        if (ratingRepository.existsByOrderId(order.getId())) {
            throw new IllegalArgumentException("You have already rated this order");
        }

        Vendor vendor = order.getVendor();

        Rating rating = Rating.builder()
                .customer(customer)
                .vendor(vendor)
                .order(order)
                .stars(request.getStars())
                .review(request.getReview())
                .build();

        ratingRepository.save(rating);
        return toResponse(rating);
    }

    public VendorRatingSummary getVendorRatings(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));

        List<Rating> ratings = ratingRepository.findByVendorIdOrderByCreatedAtDesc(vendorId);
        Double average = ratingRepository.findAverageStarsByVendorId(vendorId);

        List<RatingResponse> responses = ratings.stream()
                .map(this::toResponse)
                .toList();

        return VendorRatingSummary.builder()
                .vendorId(vendor.getId())
                .vendorName(vendor.getName())
                .averageStars(average != null ? Math.round(average * 10.0) / 10.0 : 0.0)
                .totalRatings(ratings.size())
                .reviews(responses)
                .build();
    }

    public List<RatingResponse> getMyRatings(User customer) {
        return ratingRepository.findByVendorIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private RatingResponse toResponse(Rating rating) {
        return RatingResponse.builder()
                .id(rating.getId())
                .customerName(rating.getCustomer().getFullName())
                .vendorId(rating.getVendor().getId())
                .vendorName(rating.getVendor().getName())
                .orderId(rating.getOrder().getId())
                .stars(rating.getStars())
                .review(rating.getReview())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}