package com.clickbus.places_api;

import javax.transaction.Transactional;

import com.clickbus.places_api.models.Place;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointCreatePlaceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaceRepository placeRepository;

    private String endpoint = "/api/v1.0/places/new";

    @Test
    @Order(1)
    public void UnauthorizedTest() throws Exception {

        // GET without Token
        mockMvc.perform(
            get(endpoint)
        ).andExpect(status().isUnauthorized());

        // GET with Invalid Token
        mockMvc.perform(
            get(endpoint)
            .header("Authorization", "invalidjwt")
        ).andExpect(status().isUnauthorized());

        // POST without Token
        mockMvc.perform(
            post(endpoint)
        ).andExpect(status().isUnauthorized());

        // POST with invalid Token
        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "invalidjwt")
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    public void AuthorizedGETTest() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        // Authorized GET
        mockMvc.perform(
            get(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isNotFound()); // Server will think im searching by slug for "new"

    }

    @Test
    @Order(3)
    public void AuthorizedTest() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        Place p = new Place();
        p.setName("Test Name");
        p.setSlug("test_slug");
        p.setCity("Test City");
        p.setState("Test State");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p))
        ).andExpect(status().isCreated());

        assertTrue(placeRepository.count() > 0);

        Place p2 = placeRepository.findBySlug(p.getSlug()).get();

        assertEquals(p.getName(), p2.getName());
        assertEquals(p.getSlug(), p2.getSlug());
        assertEquals(p.getCity(), p2.getCity());
        assertEquals(p.getState(), p2.getState());
    }

    @Test
    @Order(4)
    public void TestWithoutName() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        Place p = new Place();
        p.setSlug("test_slug");
        p.setCity("Test City");
        p.setState("Test State");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p))
        ).andExpect(status().isBadRequest());

        assertFalse(placeRepository.count() > 0);
        assertThrows(NoSuchElementException.class, () -> placeRepository.findBySlug("test_slug").get());
    }

    @Test
    @Order(5)
    public void TestWithoutCity() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        Place p = new Place();
        p.setName("Test Name");
        p.setSlug("test_slug");
        p.setState("Test State");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p))
        ).andExpect(status().isBadRequest());

        assertFalse(placeRepository.count() > 0);
        assertThrows(NoSuchElementException.class, () -> placeRepository.findBySlug("test_slug").get());
    }

    @Test
    @Order(6)
    public void TestWithoutSlug() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        Place p = new Place();
        p.setName("Test Name");
        p.setCity("Test City");
        p.setState("Test State");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p))
        ).andExpect(status().isBadRequest());

        assertFalse(placeRepository.count() > 0);
        assertThrows(NoSuchElementException.class, () -> placeRepository.findBySlug("test_slug").get());
    }

    @Test
    @Order(7)
    public void TestWithoutState() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        Place p = new Place();
        p.setName("Test Name");
        p.setSlug("test_slug");
        p.setCity("Test City");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p))
        ).andExpect(status().isBadRequest());

        assertFalse(placeRepository.count() > 0);
        assertThrows(NoSuchElementException.class, () -> placeRepository.findBySlug("test_slug").get());
    }

    @Test
    @Order(8)
    public void TestWithSpaceOnSlug() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        Place p = new Place();
        p.setName("Test Name");
        p.setSlug("test slug");
        p.setCity("Test City");
        p.setState("Test State");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p))
        ).andExpect(status().isBadRequest());

        assertFalse(placeRepository.count() > 0);
        assertThrows(NoSuchElementException.class, () -> placeRepository.findBySlug("test_slug").get());
    }

    @Test
    @Order(9)
    public void TestWithReservedKeywordOnSlug() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        List<String> lstReservedKeywords = Arrays.asList(new String[]{"new", "search", "edit"});

        Place p = new Place();
        p.setName("Test Name");
        p.setSlug(lstReservedKeywords.get(new Random().nextInt(lstReservedKeywords.size())));
        p.setCity("Test City");
        p.setState("Test State");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p))
        ).andExpect(status().isBadRequest());

        assertFalse(placeRepository.count() > 0);
        assertThrows(NoSuchElementException.class, () -> placeRepository.findBySlug("test_slug").get());
    }

    @Test
    @Order(10)
    public void TestDuplicate() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        Place p1 = new Place();
        p1.setName("Test Name");
        p1.setSlug("test_slug");
        p1.setCity("Test City");
        p1.setState("Test State");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p1))
        ).andExpect(status().isCreated());

        assertTrue(placeRepository.count() > 0);
        assertNotNull(placeRepository.findBySlug("test_slug").get());

        // Repeated slug
        Place p2 = new Place();
        p2.setName("Different Name");
        p2.setSlug("test_slug");
        p2.setCity("Different City");
        p2.setState("Different State");

        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(p2))
        ).andExpect(status().isConflict());

    }
}