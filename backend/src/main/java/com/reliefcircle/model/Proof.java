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
@Table(name = "proofs")
public class Proof {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "charity_id")
    private Charity charity;

    @Column(name = "file_url")
    private String fileUrl;

    @Column
    private String description;

    @Column(name = "uploaded_at")
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();
} 