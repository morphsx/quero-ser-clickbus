package com.clickbus.places_api;

import javax.transaction.Transactional;

import com.clickbus.places_api.repository.PlaceRepository;
import com.clickbus.places_api.security.models.JwtRequest;
import com.clickbus.places_api.security.models.JwtResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointFetchPlaceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlaceRepository placeRepository;
    
    @Test
    @Order(1)
    public void UnauthorizedTest() throws Exception {

        mockMvc.perform(
            get("/api/v1.0/places/test_slug")
        ).andExpect(status().isUnauthorized());

        mockMvc.perform(
            get("/api/v1.0/places/test_slug")
            .header("Authorization", "invalidjwt")
        ).andExpect(status().isUnauthorized());

    }

    @Test
    @Order(2)
    public void AuthorizedUnknownSlugTest() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtRequest jwtRequest = new JwtRequest("test", "testpwd");
        JwtResponse jwtResponse = Utils.getToken(mockMvc, jwtRequest, objectMapper);

        mockMvc.perform(
            get("/api/v1.0/places/test_slug")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isNotFound());

    }

    @Test
    @Order(3)
    public void AuthorizedSlugTest() throws Exception {

        Utils.CreateTestUser(mockMvc);
        Utils.CreateTestPlace(placeRepository);
        JwtRequest jwtRequest = new JwtRequest("test", "testpwd");
        JwtResponse jwtResponse = Utils.getToken(mockMvc, jwtRequest, objectMapper);

        mockMvc.perform(
            get("/api/v1.0/places/test_slug")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isOk());
    }

}