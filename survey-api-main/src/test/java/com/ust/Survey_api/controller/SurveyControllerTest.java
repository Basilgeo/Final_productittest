package com.ust.Survey_api.controller;

import com.ust.Survey_api.exception.SetNotFoundException;
import com.ust.Survey_api.feign.FullResponse;
import com.ust.Survey_api.feign.PostDto;
import com.ust.Survey_api.feign.SurveyRequestDto;
import com.ust.Survey_api.model.Emails;
import com.ust.Survey_api.model.Survey;
import com.ust.Survey_api.repository.SurveyRepository;
import com.ust.Survey_api.service.SurveyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SurveyControllerTest {

    @Mock
    private SurveyService surveyService;

    @Mock
    private SurveyRepository surveyRepository;

    @InjectMocks
    private SurveyController surveyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddEmails_SetNotFoundException() {
        // Mocking the repository to return null, simulating a survey not found
        when(surveyRepository.findBySurveyid(anyLong())).thenReturn(null);

        // Assert that SetNotFoundException is thrown
        assertThrows(SetNotFoundException.class, () -> {
            surveyController.addEmails(1L, Collections.emptyList());
        }, "Expected SetNotFoundException to be thrown, but nothing was thrown.");

        // Verify that the repository method was called
        verify(surveyRepository, times(1)).findBySurveyid(anyLong());
    }

    @Test
    public void testGetEmails_SetNotFoundException() {
        // Mocking the repository to return null, simulating a survey not found
        when(surveyRepository.findBySurveyid(anyLong())).thenReturn(null);

        // Assert that SetNotFoundException is thrown
        assertThrows(SetNotFoundException.class, () -> {
            surveyController.getEmails(1L);
        }, "Expected SetNotFoundException to be thrown, but nothing was thrown.");

        // Verify that the repository method was called
        verify(surveyRepository, times(1)).findBySurveyid(anyLong());
    }

    @Test
    public void testAddEmails_Success() {
        // Mocking the repository to return a valid Survey object
        Survey survey = new Survey();
        when(surveyRepository.findBySurveyid(anyLong())).thenReturn(survey);

        // Mocking the service method to return a list of emails
        List<Emails> emailsList = Collections.singletonList(new Emails());
        when(surveyService.addEmails(anyLong(), anyList())).thenReturn(emailsList);

        // Calling the controller method
        ResponseEntity<List<Emails>> response = surveyController.addEmails(1L, Collections.emptyList());

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(emailsList, response.getBody());

        // Verify that the repository method was called
        verify(surveyRepository, times(1)).findBySurveyid(anyLong());
        // Verify that the service method was called
        verify(surveyService, times(1)).addEmails(anyLong(), anyList());
    }

    @Test
    public void testGetEmails_Success() {
        // Mocking the repository to return a valid Survey object
        Survey survey = new Survey();
        when(surveyRepository.findBySurveyid(anyLong())).thenReturn(survey);

        // Mocking the service method to return a list of emails
        List<Emails> emailsList = Collections.singletonList(new Emails());
        when(surveyService.getEmails(anyLong())).thenReturn(emailsList);

        // Calling the controller method
        ResponseEntity<List<Emails>> response = surveyController.getEmails(1L);

        // Assertions
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(emailsList, response.getBody());

        // Verify that the repository method was called
        verify(surveyRepository, times(1)).findBySurveyid(anyLong());
        // Verify that the service method was called
        verify(surveyService, times(1)).getEmails(anyLong());
    }
    @Test
    public void testAddSurvey_Success() {
        SurveyRequestDto request = new SurveyRequestDto("requestor", "companyName", 1L);
        PostDto postDto = new PostDto(1L, 1L, "requestor", "companyName", 1L, null, null);

        when(surveyService.addSurvey(any(SurveyRequestDto.class))).thenReturn(postDto);

        ResponseEntity<PostDto> response = surveyController.addSurvey(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(postDto, response.getBody());
    }

    @Test
    public void testAddSurvey_SetNotFoundException() {
        when(surveyService.addSurvey(any(SurveyRequestDto.class))).thenThrow(new SetNotFoundException("Set name not found."));

        SurveyRequestDto request = new SurveyRequestDto("requestor", "companyName", 1L);

        assertThrows(SetNotFoundException.class, () -> {
            surveyController.addSurvey(request);
        });
    }

    @Test
    public void testGetSurveys_Success() {
        FullResponse fullResponse = new FullResponse(1L, 1L, "requestor", "companyName", 1L, null, null, null);
        when(surveyService.getSurveys()).thenReturn(Arrays.asList(fullResponse));

        ResponseEntity<List<FullResponse>> response = surveyController.getSurveys();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetSurveyById_Success() {
        FullResponse fullResponse = new FullResponse(1L, 1L, "requestor", "companyName", 1L, null, null, null);
        when(surveyService.getSurveyById(anyLong())).thenReturn(fullResponse);

        ResponseEntity<?> response = surveyController.getSurveyById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(fullResponse, response.getBody());
    }

    @Test
    public void testGetSurveyById_SetNotFoundException() {
        when(surveyService.getSurveyById(anyLong())).thenThrow(new SetNotFoundException("Invalid surveyId"));

        assertThrows(SetNotFoundException.class, () -> {
            surveyController.getSurveyById(1L);
        });
    }
}
