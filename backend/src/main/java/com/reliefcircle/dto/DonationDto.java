package com.reliefcircle.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
@NoArgsConstructor
@ToString
@Data
public class DonationDto {

  private Long id;
  private String paypalId;
  private String email;
  private double amount;
  private String status;
  private Date paymentDate;
  private String currencyCode;
}
