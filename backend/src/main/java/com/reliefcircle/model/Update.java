package com.reliefcircle.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "updates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"charity", "ratings"})
public class Update {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fundraiser_id", nullable = false)
    private User fundraiser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charity_id", nullable = false)
    private Charity charity;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "update", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UpdateRating> ratings = new ArrayList<>();

    @Column(name = "average_rating")
    @Builder.Default
    private Double averageRating = 0.0;

    @Column(name = "rating_count")
    @Builder.Default
    private Integer ratingCount = 0;

    @Column(name = "is_approved")
    @Builder.Default
    private Boolean isApproved = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Recalculates the average rating based on all ratings
     */
    public void recalculateAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            this.averageRating = 0.0;
            this.ratingCount = 0;
            return;
        }

        double sum = 0;
        for (UpdateRating rating : ratings) {
            sum += rating.getRating();
        }
        
        this.averageRating = sum / ratings.size();
        this.ratingCount = ratings.size();
    }

    /**
     * Adds a rating to this update and recalculates the average
     */
    public void addRating(UpdateRating rating) {
        ratings.add(rating);
        rating.setUpdate(this);
        recalculateAverageRating();
    }

    /**
     * Removes a rating from this update and recalculates the average
     */
    public void removeRating(UpdateRating rating) {
        ratings.remove(rating);
        rating.setUpdate(null);
        recalculateAverageRating();
    }
}