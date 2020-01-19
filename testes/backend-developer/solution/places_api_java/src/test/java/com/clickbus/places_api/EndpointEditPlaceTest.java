package com.clickbus.places_api;

import javax.transaction.Transactional;

import com.clickbus.places_api.models.Place;
import com.clickbus.places_api.objects.EditPlaceData;
import com.clickbus.places_api.objects.EditPlaceField;
import com.clickbus.places_api.objects.EditPlaceObject;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EndpointEditPlaceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaceRepository placeRepository;

    private String endpoint = "/api/v1.0/places/edit/";

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

        // POST without Token
        mockMvc.perform(
            put(endpoint)
        ).andExpect(status().isUnauthorized());

        // PUT with invalid Token
        mockMvc.perform(
            put(endpoint)
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
        ).andExpect(status().isNotFound()); // Server will think im searching by slug for "edit"

    }

    @Test
    @Order(3)
    public void AuthorizedPOSTTest() throws Exception {

        Utils.CreateTestUser(mockMvc);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        // Authorized GET
        mockMvc.perform(
            post(endpoint)
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
        ).andExpect(status().isMethodNotAllowed());

    }

    @Test
    @Order(4)
    public void AuthorizedTest() throws Exception {

        Utils.CreateTestUser(mockMvc);
        Place p = Utils.CreateTestPlace(placeRepository);
        long originalId = p.getId();
        String originalName = p.getName();
        String originalSlug = p.getSlug();
        String originalCity = p.getCity();
        String originalState = p.getState();
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        EditPlaceData editData = new EditPlaceData();
        EditPlaceField editField = new EditPlaceField();
        EditPlaceObject editObject = new EditPlaceObject();

        editData.setCurrent_value(p.getName());
        editData.setNew_value("Changed Name");

        editField.setName("name");
        editField.setData(editData);

        List<EditPlaceField> lstFields = new ArrayList<EditPlaceField>();
        lstFields.add(editField);

        editObject.setFields(lstFields);

        mockMvc.perform(
            put(endpoint + p.getId())
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editObject))
        ).andExpect(status().isOk());

        Place p2 = placeRepository.findBySlug(p.getSlug()).get();

        // Assert name changed
        assertFalse(originalName.equals(p2.getName()));
        assertEquals(originalId, p2.getId());
        assertEquals(p2.getName(), "Changed Name");
        assertEquals(originalSlug, p2.getSlug());
        assertEquals(originalCity, p2.getCity());
        assertEquals(originalState, p2.getState());
    }

    @Test
    @Order(4)
    public void TestWrongCurrentValue() throws Exception {

        Utils.CreateTestUser(mockMvc);
        Place p = Utils.CreateTestPlace(placeRepository);
        long originalId = p.getId();
        String originalName = p.getName();
        String originalSlug = p.getSlug();
        String originalCity = p.getCity();
        String originalState = p.getState();
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        EditPlaceData editData = new EditPlaceData();
        EditPlaceField editField = new EditPlaceField();
        EditPlaceObject editObject = new EditPlaceObject();

        editData.setCurrent_value("Wrong Value");
        editData.setNew_value("Changed Name");

        editField.setName("name");
        editField.setData(editData);

        List<EditPlaceField> lstFields = new ArrayList<EditPlaceField>();
        lstFields.add(editField);

        editObject.setFields(lstFields);

        MvcResult result = mockMvc.perform(
            put(endpoint + p.getId())
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editObject))
        ).andExpect(status().isBadRequest())
         .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("error_message"));

        Place p2 = placeRepository.findBySlug(p.getSlug()).get();

        // Assert nothing changed
        assertFalse(p2.getName().equals("Changed Name"));
        assertEquals(originalId, p2.getId());
        assertEquals(originalName, p2.getName());
        assertEquals(originalSlug, p2.getSlug());
        assertEquals(originalCity, p2.getCity());
        assertEquals(originalState, p2.getState());
    }

    @Test
    @Order(5)
    public void TestWithoutCurrentValue() throws Exception {

        Utils.CreateTestUser(mockMvc);
        Place p = Utils.CreateTestPlace(placeRepository);
        long originalId = p.getId();
        String originalName = p.getName();
        String originalSlug = p.getSlug();
        String originalCity = p.getCity();
        String originalState = p.getState();
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        EditPlaceData editData = new EditPlaceData();
        EditPlaceField editField = new EditPlaceField();
        EditPlaceObject editObject = new EditPlaceObject();

        editData.setNew_value("Changed Name");

        editField.setName("name");
        editField.setData(editData);

        List<EditPlaceField> lstFields = new ArrayList<EditPlaceField>();
        lstFields.add(editField);

        editObject.setFields(lstFields);

        MvcResult result = mockMvc.perform(
            put(endpoint + p.getId())
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editObject))
        ).andExpect(status().isBadRequest())
         .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("error_message"));

        Place p2 = placeRepository.findBySlug(p.getSlug()).get();

        // Assert nothing changed
        assertFalse(p2.getName().equals("Changed Name"));
        assertEquals(originalId, p2.getId());
        assertEquals(originalName, p2.getName());
        assertEquals(originalSlug, p2.getSlug());
        assertEquals(originalCity, p2.getCity());
        assertEquals(originalState, p2.getState());
    }

    @Test
    @Order(6)
    public void TestWithoutNewValue() throws Exception {

        Utils.CreateTestUser(mockMvc);
        Place p = Utils.CreateTestPlace(placeRepository);
        long originalId = p.getId();
        String originalName = p.getName();
        String originalSlug = p.getSlug();
        String originalCity = p.getCity();
        String originalState = p.getState();
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        EditPlaceData editData = new EditPlaceData();
        EditPlaceField editField = new EditPlaceField();
        EditPlaceObject editObject = new EditPlaceObject();

        editData.setCurrent_value(p.getName());

        editField.setName("name");
        editField.setData(editData);

        List<EditPlaceField> lstFields = new ArrayList<EditPlaceField>();
        lstFields.add(editField);

        editObject.setFields(lstFields);

        MvcResult result = mockMvc.perform(
            put(endpoint + p.getId())
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editObject))
        ).andExpect(status().isBadRequest())
         .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("error_message"));

        Place p2 = placeRepository.findBySlug(p.getSlug()).get();

        // Assert nothing changed
        assertEquals(originalId, p2.getId());
        assertEquals(originalName, p2.getName());
        assertEquals(originalSlug, p2.getSlug());
        assertEquals(originalCity, p2.getCity());
        assertEquals(originalState, p2.getState());
    }

    @Test
    @Order(7)
    public void TestWithSpaceOnSlug() throws Exception {

        Utils.CreateTestUser(mockMvc);
        Place p = Utils.CreateTestPlace(placeRepository);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        EditPlaceData editData = new EditPlaceData();
        EditPlaceField editField = new EditPlaceField();
        EditPlaceObject editObject = new EditPlaceObject();

        editData.setCurrent_value(p.getSlug());
        editData.setNew_value("Slug with spaces");

        editField.setName("slug");
        editField.setData(editData);

        List<EditPlaceField> lstFields = new ArrayList<EditPlaceField>();
        lstFields.add(editField);

        editObject.setFields(lstFields);

        MvcResult result = mockMvc.perform(
            put(endpoint + p.getId())
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editObject))
        ).andExpect(status().isBadRequest())
         .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("error_message"));

        // Can't assert model instance changes since space validation happens on model save...
        // Instance will be changed, but wont be persisted.
        // Should be pre-validated on view also?
    }

    @Test
    @Order(8)
    public void TestWithReservedKeyword() throws Exception {

        List<String> lstReservedKeywords = Arrays.asList(new String[]{"new", "search", "edit"});
        String reservedKeyword = lstReservedKeywords.get(new Random().nextInt(lstReservedKeywords.size()));

        Utils.CreateTestUser(mockMvc);
        Place p = Utils.CreateTestPlace(placeRepository);
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        EditPlaceData editData = new EditPlaceData();
        EditPlaceField editField = new EditPlaceField();
        EditPlaceObject editObject = new EditPlaceObject();

        editData.setCurrent_value(p.getSlug());
        editData.setNew_value(reservedKeyword);

        editField.setName("slug");
        editField.setData(editData);

        List<EditPlaceField> lstFields = new ArrayList<EditPlaceField>();
        lstFields.add(editField);

        editObject.setFields(lstFields);

        MvcResult result = mockMvc.perform(
            put(endpoint + p.getId())
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editObject))
        ).andExpect(status().isBadRequest())
         .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("error_message"));

        // Can't assert model instance changes since keyword validation happens on model save...
        // Instance will be changed, but wont be persisted.
        // Should be pre-validated on view also?
    }

    @Test
    @Order(8)
    public void TestUnknownField() throws Exception {

        List<String> lstReservedKeywords = Arrays.asList(new String[]{"new", "search", "edit"});

        Utils.CreateTestUser(mockMvc);
        Place p = Utils.CreateTestPlace(placeRepository);
        long originalId = p.getId();
        String originalName = p.getName();
        String originalSlug = p.getSlug();
        String originalCity = p.getCity();
        String originalState = p.getState();
        JwtResponse jwtResponse = Utils.getToken(mockMvc, new JwtRequest("test", "testpwd"), objectMapper);

        EditPlaceData editData = new EditPlaceData();
        EditPlaceField editField = new EditPlaceField();
        EditPlaceObject editObject = new EditPlaceObject();

        editData.setCurrent_value(p.getSlug());
        editData.setNew_value(lstReservedKeywords.get(new Random().nextInt(lstReservedKeywords.size())));

        editField.setName("unknown_field");
        editField.setData(editData);

        List<EditPlaceField> lstFields = new ArrayList<EditPlaceField>();
        lstFields.add(editField);

        editObject.setFields(lstFields);

        MvcResult result = mockMvc.perform(
            put(endpoint + p.getId())
            .header("Authorization", "JWT " + jwtResponse.getAccess_token())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(editObject))
        ).andExpect(status().isBadRequest())
         .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("error_message"));

        Place p2 = placeRepository.findBySlug(p.getSlug()).get();

        // Assert nothing changed
        assertEquals(originalId, p2.getId());
        assertEquals(originalName, p2.getName());
        assertEquals(originalSlug, p2.getSlug());
        assertEquals(originalCity, p2.getCity());
        assertEquals(originalState, p2.getState());
    }

}