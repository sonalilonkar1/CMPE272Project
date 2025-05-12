package com.reliefcircle.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.model.User;
import com.reliefcircle.model.User.UserRole;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CharityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CharityRepository charityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User fundraiser;
    private CharityDto charityDto;

    @BeforeEach
    void setUp() {
        fundraiser = userRepository.save(User.builder()
                .email("fundraiser@test.com")
                .role(UserRole.FUNDRAISER)
                .build());

        charityDto = CharityDto.builder()
                .name("Test Charity")
                .description("Test Description")
                .targetAmount(BigDecimal.valueOf(10000))
                .build();
    }

    @Test
    @WithMockUser(username = "fundraiser@test.com", roles = "FUNDRAISER")
    void createAndRetrieveCharity() throws Exception {
        // Create charity
        String response = mockMvc.perform(post("/api/charities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(charityDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long charityId = extractCharityId(response);

        // Retrieve charity
        mockMvc.perform(get("/api/charities/" + charityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(charityDto.getName()));
    }

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private Long extractCharityId(String response) throws Exception {
        return objectMapper.readTree(response)
                .get("id")
                .asLong();
    }
}