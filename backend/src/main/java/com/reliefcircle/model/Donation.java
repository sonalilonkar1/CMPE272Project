package com.reliefcircle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
@Builder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_status", nullable = true)
    private String paymentStatus;

    @Column(name = "transaction_id", nullable = true)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charity_id", nullable = false)
    private Charity charity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private User donor;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "stripe_order_id", nullable = true)
    private String stripeOrderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    @Builder.Default
    private DonationStatus status = DonationStatus.PENDING;

    public enum DonationStatus {
        PENDING,
        COMPLETED,
        FAILED
    }
}