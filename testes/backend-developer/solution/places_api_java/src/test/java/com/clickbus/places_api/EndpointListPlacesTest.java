package com.clickbus.places_api;

import javax.transaction.Transactional;

import com.clickbus.places_api.security.models.JwtRequest;
import com.clickbus.places_api.security.models.JwtResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointListPlacesTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String endpoint = "/api/v1.0/places";

    @Test
    @Order(1)
    public void UnauthorizedTest() throws Exception {

        // GET Without Token
        mockMvc.perform(
            get(endpoint)
        ).andExpect(status().isUnauthorized());

        // GET with Invalid Token
        mockMvc.perform(
            get(endpoint)
            .header("Authorization", "invalidjwt")
        ).andExpect(status().isUnauthorized());

        // POST Without Token
        mockMvc.perform(
            post(endpoint)
        ).andExpect(status().isUnauthorized());

        // POST with Invalid Token
        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "invalidjwt")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    public void AuthorizedTest() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        // Authorized GET
        MvcResult result = mockMvc.perform(
            get(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isOk())
         .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("places"));

        // Authorized POST
        result = mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isMethodNotAllowed())
         .andReturn();

        response = result.getResponse().getContentAsString();
        assertFalse(response.contains("places"));
    }


}