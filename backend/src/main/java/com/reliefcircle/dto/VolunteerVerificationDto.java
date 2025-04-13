package com.reliefcircle.dto;

import lombok.*;
import java.util.Date;
import java.util.UUID;


@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
@ToString
public class VolunteerVerificationDto {

    private Long id;
    private UUID volunteerId;
    private Long charityId;
    private String status;
    private String comments;
    private Date submittedAt;
}