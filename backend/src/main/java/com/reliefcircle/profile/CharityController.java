package com.reliefcircle.profile;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.service.CharityService;

@RestController
@RequestMapping("charity")
@CrossOrigin("*")
public class CharityController {

  private final CharityService charityService;

  @Autowired
  public CharityController(CharityService charityService) {
    this.charityService = charityService;
  }

  @GetMapping
  List<CharityDto> getCharities() {
    return charityService.getAllCharities();
  }

  @PutMapping(path="/charity_approval/{id}")
  public boolean getMessage(@PathVariable("id") long id) {
    return charityService.approveCharity(id);
  }

  @GetMapping(path = "/approved_c")
  List<CharityDto> getApprovedCharities() {
    return charityService.getApprovedCharities();
  }

  @GetMapping(path = "/donations")
  List<DonationDto> getDonations() {
    return charityService.getDonations();
  }

  @PostMapping(consumes= MediaType.MULTIPART_FORM_DATA_VALUE,
      produces=MediaType.APPLICATION_JSON_VALUE)
  CharityDto registerCharity(@RequestParam("cname") String cname,
      @RequestParam("location") String location,
      @RequestParam("email") String email,
      @RequestParam("description") String description,
      @RequestParam("file") MultipartFile file) {
    try
    {
      return charityService.registerCharity(CharityDto.builder()
              .cname(cname)
              .location(location)
              .email(email)
              .description(description)
              .file(file)
              .build());
    }
    catch(Exception ex)
    {
      System.out.println(ex.toString());
      throw ex;
    }

  }
}
