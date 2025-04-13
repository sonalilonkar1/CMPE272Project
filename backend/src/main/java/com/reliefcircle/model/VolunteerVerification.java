package com.reliefcircle.model;

import javax.persistence.*;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@ToString
@Builder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "volunteer_verification")
public class VolunteerVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private UserProfile volunteer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charity_id", nullable = false)
    private Charity charity;

    @Column(name = "verification_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus status;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submitted_at")
    private Date submittedAt;
}