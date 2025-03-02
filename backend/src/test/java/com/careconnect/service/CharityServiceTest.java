// package com.careconnect.service;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// import com.careconnect.datastore.CharityRepository;
// import com.careconnect.datastore.DonationRepository;
// import com.careconnect.dto.CharityDto;
// import com.careconnect.model.Charity;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.web.multipart.MultipartFile;

// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;

// class CharityServiceTest {

//     @Mock
//     private CharityRepository charityRepository;

//     @Mock
//     private DonationRepository donationRepository;

//     @Mock
//     private MultipartFile mockFile;

//     @InjectMocks
//     private CharityService charityService;

//     @BeforeEach
//     void setUp() {
//         // MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     void testRegisterCharity_Success() {
//         CharityDto dto = CharityDto.builder()
//                 .cname("Test Charity")
//                 .email("test@example.com")
//                 .description("Helping People")
//                 .location("New York")
//                 .file(mockFile)
//                 .build();

//         Charity charity = Charity.builder()
//                 .charityName(dto.getCname())
//                 .approved(false)
//                 .description(dto.getDescription())
//                 .email(dto.getEmail())
//                 .location(dto.getLocation())
//                 .fileLink("http://mock-cloudfront.com/test-file")
//                 .build();

//         when(charityRepository.save(any(Charity.class))).thenReturn(charity);
//         when(mockFile.getOriginalFilename()).thenReturn("test-file.jpg");
//         // mockStatic(AWSService.class);
//         // when(AWSService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(true);
//         // when(AWSService.pubMessageToAdmin(any(CharityDto.class))).thenReturn(true);

//         CharityDto result = charityService.registerCharity(dto);

//         assertNotNull(result);
//         assertEquals(dto.getCname(), result.getCname());
//         assertEquals(dto.getEmail(), result.getEmail());
//         verify(charityRepository, times(1)).save(any(Charity.class));
//     }

//     @Test
//     void testRegisterCharity_Failure() {
//         CharityDto dto = CharityDto.builder()
//                 .cname("Test Charity")
//                 .email("test@example.com")
//                 .description("Helping People")
//                 .location("New York")
//                 .file(mockFile)
//                 .build();

//         when(mockFile.getOriginalFilename()).thenReturn("test-file.jpg");
//         // mockStatic(AWSService.class);
//         // when(AWSService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(false);

//         Exception exception = assertThrows(RuntimeException.class, () -> {
//             charityService.registerCharity(dto);
//         });

//         assertTrue(exception.getMessage().contains("Cannot Upload"));
//     }

//     @Test
//     void testGetAllCharities() {
//         Charity charity1 = Charity.builder().charityName("Charity1").approved(true).build();
//         Charity charity2 = Charity.builder().charityName("Charity2").approved(false).build();
//         List<Charity> charityList = Arrays.asList(charity1, charity2);

//         when(charityRepository.findAll()).thenReturn(charityList);

//         List<CharityDto> result = charityService.getAllCharities();

//         assertEquals(2, result.size());
//     }

//     @Test
//     void testGetApprovedCharities() {
//         Charity charity1 = Charity.builder().charityName("Charity1").approved(true).build();
//         Charity charity2 = Charity.builder().charityName("Charity2").approved(false).build();
//         List<Charity> charityList = Arrays.asList(charity1, charity2);

//         when(charityRepository.findAll()).thenReturn(charityList);

//         List<CharityDto> result = charityService.getApprovedCharities();

//         assertEquals(1, result.size());
//         assertTrue(result.get(0).isApproved());
//     }

//     @Test
//     void testApproveCharity() {
//         Charity charity = Charity.builder().charityName("Charity1").approved(false).build();

//         when(charityRepository.findById(anyLong())).thenReturn(Optional.of(charity));

//         boolean result = charityService.approveCharity(1L);

//         assertTrue(result);
//         assertTrue(charity.isApproved());
//         verify(charityRepository, times(1)).save(charity);
//     }
// }
