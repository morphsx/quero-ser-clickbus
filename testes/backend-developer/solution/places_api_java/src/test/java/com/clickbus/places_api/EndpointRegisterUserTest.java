package com.clickbus.places_api;

import com.clickbus.places_api.models.User;
import com.clickbus.places_api.repository.UserRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointRegisterUserTest {

    @Autowired
	private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
	void CreateUser() throws Exception {
		User user = new User();
		user.setId(1L);
		user.setUsername("test");
		user.setPassword("testpwd");

		List<User> lst = Arrays.asList(user);

		mockMvc.perform(
			post("/auth/register")
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"username\": \"test\", \"password\": \"testpwd\"}")
		).andExpect(status().isCreated());

		assertTrue(userRepository.findAll().size() > 0);
        assertEquals(lst.size(), userRepository.findAll().size());

        // Creating repeated User
        mockMvc.perform(
            post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\": \"test\", \"password\": \"testpwd\"}")
        ).andExpect(status().isConflict());
    }

}