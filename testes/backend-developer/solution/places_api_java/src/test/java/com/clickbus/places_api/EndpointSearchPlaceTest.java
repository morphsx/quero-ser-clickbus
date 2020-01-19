package com.clickbus.places_api;

import javax.transaction.Transactional;

import com.clickbus.places_api.repository.PlaceRepository;
import com.clickbus.places_api.security.models.JwtRequest;
import com.clickbus.places_api.security.models.JwtResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointSearchPlaceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaceRepository placeRepository;

    private String endpoint = "/api/v1.0/places/search";

    @Test
    @Order(1)
    public void UnauthorizedTest() throws Exception {

        // GET Without Token
        mockMvc.perform(
            get(endpoint + "/TestPlace")
        ).andExpect(status().isUnauthorized());

        // GET with Invalid Token
        mockMvc.perform(
            get(endpoint + "/TestPlace")
            .header("Authorization", "invalidjwt")
        ).andExpect(status().isUnauthorized());

        // POST Without Token
        mockMvc.perform(
            post(endpoint + "/TestPlace")
        ).andExpect(status().isUnauthorized());

        // POST with Invalid Token
        mockMvc.perform(
            post(endpoint + "/TestPlace")
            .header("Authorization", "invalidjwt")
        ).andExpect(status().isUnauthorized());

    }

    @Test
    @Order(2)
    public void AuthorizedTest() throws Exception {

        Utils.CreateTestUser(mockMvc);
        Utils.CreateTestPlace(placeRepository);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        // Authorized GET with unknown place
        MvcResult result = mockMvc.perform(
            get(endpoint + "/TestPlace")
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isOk())
         .andReturn();

        String response = result.getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        JsonNode placesNode = node.get("places");

        assertTrue(placesNode.size() == 0);

        // Authorized GET with known place
        result = mockMvc.perform(
            get(endpoint + "/Test Place")
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isOk())
         .andReturn();

        response = result.getResponse().getContentAsString();

        node = objectMapper.readTree(response);
        placesNode = node.get("places");

        assertTrue(placesNode.size() > 0);


        // Authorized POST
        mockMvc.perform(
            post(endpoint + "Test Place")
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isMethodNotAllowed());

    }
}