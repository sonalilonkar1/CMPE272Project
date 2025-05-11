package com.reliefcircle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "charities")
public class Charity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fundraiser_id")
    private User fundraiser;

    @Column(nullable = false)
    private String name;

    @Column(name = "organization_name")
    private String organizationName;

    @Column
    private String description;

    @Column
    private String category;

    @Column(name = "target_amount", precision = 12, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "raised_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal raisedAmount = BigDecimal.ZERO;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "fundraiser_stripe_id")
    private String fundraiserStripeId;

    @OneToMany(mappedBy = "charity", fetch = FetchType.LAZY)
    private List<Donation> donations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Charity charity = (Charity) o;
        return id.equals(charity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}