package com.reliefcircle.model;

public enum DonationStatus {
	PENDING,
    COMPLETED,
    FAILED,
    REFUNDED;

    @Override
    public String toString() {
        return name();
    }
}
