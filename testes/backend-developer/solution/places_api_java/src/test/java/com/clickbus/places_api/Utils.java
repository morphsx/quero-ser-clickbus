package com.clickbus.places_api;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.clickbus.places_api.models.Place;
import com.clickbus.places_api.repository.PlaceRepository;
import com.clickbus.places_api.security.models.JwtRequest;
import com.clickbus.places_api.security.models.JwtResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;

public class Utils {
    public static void CreateTestUser(MockMvc mockMvc) throws Exception {
        mockMvc.perform(
			post("/auth/register")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"username\": \"test\", \"password\": \"testpwd\"}")
		).andExpect(status().isCreated());
	}
	
	public static JwtResponse getToken(MockMvc mockMvc, JwtRequest jwtRequest, ObjectMapper objectMapper) throws Exception {
		MvcResult result = mockMvc.perform(
			post("/auth")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(jwtRequest))
		).andExpect(status().isOk())
		 .andReturn();

		 JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
		 JwtResponse response = new JwtResponse(node.get("access_token").asText());
		 return response;
	}

	public static Place CreateTestPlace(PlaceRepository repository) {
		Place p = new Place();
		p.setName("Test Place");
		p.setSlug("test_slug");
		p.setCity("Test City");
		p.setState("Test State");
		return repository.save(p);
	}
}