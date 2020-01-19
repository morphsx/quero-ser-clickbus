package com.clickbus.places_api;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.clickbus.places_api.security.models.JwtRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointAuthTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
	private ObjectMapper objectMapper;

	@Test
	@Order(1)
    void AuthGet() throws Exception {
        mockMvc.perform(
            get("/auth")
        ).andExpect(status().isMethodNotAllowed());
    }

	@Test
	@Order(2)
    void AuthPostWithoutUser() throws Exception {
        mockMvc.perform(
            post("/auth")
        ).andExpect(status().isBadRequest());
    }

	@Test
	@Order(3)
	void AuthPostWithUnregisteredUser() throws Exception {
		JwtRequest req = new JwtRequest("notregistered", "notregistered");

		MvcResult result = mockMvc.perform(
			post("/auth")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(req))
		).andExpect(status().isNotFound())
		 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		 .andReturn();

		 String response = result.getResponse().getContentAsString();
		 assertTrue(response.contains("error_message"));
	}

	@Test
	@Order(4)
	void AuthPostWithRegisteredUser() throws Exception {
		Utils.CreateTestUser(mockMvc);
		JwtRequest req = new JwtRequest("test", "testpwd");
		MvcResult result = mockMvc.perform(
			post("/auth")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(req))
		).andExpect(status().isOk())
		 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
		 .andReturn();

		 String response = result.getResponse().getContentAsString();
		 assertTrue(response.contains("access_token"));
	}
}