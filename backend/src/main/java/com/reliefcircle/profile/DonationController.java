package com.reliefcircle.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reliefcircle.service.CharityService;

@RestController
@RequestMapping("donation")
@CrossOrigin("*")
public class DonationController {
  private final CharityService charityService;

  @Autowired
  public DonationController(CharityService charityService) {
    this.charityService = charityService;
  }

  @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE,
      produces=MediaType.APPLICATION_JSON_VALUE)
  boolean addDonation(@RequestParam("paypal") String paypal) {

    return charityService.addDonation(paypal);
  }
}
