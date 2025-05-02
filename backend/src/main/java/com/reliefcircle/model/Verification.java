package com.reliefcircle.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
@Builder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "verifications")
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "volunteer_id")
    private User volunteer;

    @ManyToOne
    @JoinColumn(name = "charity_id")
    private Charity charity;

    @ManyToOne
    @JoinColumn(name = "proof_id")
    private Proof proof;

    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    @Column
    private String comment;

    @Column(name = "reviewed_at")
    @Builder.Default
    private LocalDateTime reviewedAt = LocalDateTime.now();

    public enum VerificationStatus {
        PENDING, APPROVED, REJECTED
    }
}