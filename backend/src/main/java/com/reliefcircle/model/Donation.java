package com.reliefcircle.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "donation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "paypal_id", nullable = false)
    private String paypalId; // Changed from "paypal" to "paypal_id" to match DTO

    @Column(name = "email")
    private String email;

    @Column(name = "amount")
    private double amount;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "volunteer_opt_in")
    private boolean volunteerOptIn;

    @Column(name = "donor_id", nullable = false)
    private UUID donorId;

    @Column(name = "charity_id", nullable = false)
    private Long charityId;
}