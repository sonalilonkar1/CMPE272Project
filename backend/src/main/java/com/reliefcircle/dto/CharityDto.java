package com.reliefcircle.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@EqualsAndHashCode
@Builder
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CharityDto {

  private String cname;
  private String location;
  private String email;
  String description;
  MultipartFile file;
  boolean approved;
  String fileLink;
  Long id;

}
