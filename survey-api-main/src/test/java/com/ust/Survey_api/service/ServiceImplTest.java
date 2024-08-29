package com.ust.Survey_api.service;

import com.ust.Survey_api.exception.SetNotFoundException;
import com.ust.Survey_api.feign.*;
import com.ust.Survey_api.model.Emails;
import com.ust.Survey_api.model.Status;
import com.ust.Survey_api.model.Survey;
import com.ust.Survey_api.repository.EmailRepository;
import com.ust.Survey_api.repository.SurveyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceImplTest {

    @InjectMocks
    private ServiceImpl service;

    @Mock
    private AssessmentClient client;

    @Mock
    private SurveyRepository repo;

    @Mock
    private EmailRepository emailRepository;

    @Value("${survey.expire.time.days}")
    private int expireTimeDays = 30;  // Mocking the @Value annotation

    private SurveyRequestDto surveyRequestDto;
    private Survey survey;
    private List<SetNameDto> setNameDtoList;

    @BeforeEach
    public void setUp() {
        surveyRequestDto = new SurveyRequestDto("requestor", "companyName", 1L);

        setNameDtoList = Arrays.asList(
                new SetNameDto(1L, "Question 1", Arrays.asList(new Answer(1L, "Answer 1", "Suggestion 1"))),
                new SetNameDto(2L, "Question 2", Arrays.asList(new Answer(2L, "Answer 2", "Suggestion 2")))
        );

        survey = new Survey(1L, 1L, "requestor", "companyName", 1L, LocalDate.now(), null);
    }

    @Test
    public void testAddSurvey_Success() {
        when(client.getSet(anyLong())).thenReturn(ResponseEntity.ok(setNameDtoList));
        when(repo.save(any(Survey.class))).thenReturn(survey);

        PostDto postDto = service.addSurvey(surveyRequestDto);

        assertNotNull(postDto);
        assertEquals(survey.getSurveyid(), postDto.getSurveyid());
        assertEquals(survey.getSetid(), postDto.getSetId());
        assertEquals(survey.getRequestor(), postDto.getRequestor());
        assertEquals(survey.getCompanyName(), postDto.getCompanyName());
        assertNotNull(postDto.getSetdata());

        verify(repo, times(1)).save(any(Survey.class));
        verify(client, times(1)).getSet(anyLong());
    }

    @Test
    public void testAddSurvey_SetNotFoundException() {
        when(client.getSet(anyLong())).thenThrow(new SetNotFoundException("Set not found."));

        assertThrows(SetNotFoundException.class, () -> service.addSurvey(surveyRequestDto));
        verify(repo, never()).save(any(Survey.class));
    }

    @Test
    public void testGetSurveys_Success() {
        when(repo.findAll()).thenReturn(Collections.singletonList(survey));
        when(client.getSet(anyLong())).thenReturn(ResponseEntity.ok(setNameDtoList));

        List<FullResponse> fullResponses = service.getSurveys();

        assertNotNull(fullResponses);
        assertEquals(1, fullResponses.size());
        FullResponse fr = fullResponses.get(0);
        assertEquals(survey.getSurveyid(), fr.getSurveyid());
        assertEquals(survey.getSetid(), fr.getSetId());
        assertNotNull(fr.getSetdata());

        verify(repo, times(1)).findAll();
        verify(client, times(1)).getSet(anyLong());
    }

    @Test
    public void testGetSurveyById_Success() {
        when(repo.findBySurveyid(anyLong())).thenReturn(survey);
        when(client.getSet(anyLong())).thenReturn(ResponseEntity.ok(setNameDtoList));

        FullResponse fr = service.getSurveyById(1L);

        assertNotNull(fr);
        assertEquals(survey.getSurveyid(), fr.getSurveyid());
        assertEquals(survey.getSetid(), fr.getSetId());
        assertNotNull(fr.getSetdata());

        verify(repo, times(1)).findBySurveyid(anyLong());
        verify(client, times(1)).getSet(anyLong());
    }

    @Test
    public void testGetSurveyById_SetNotFoundException() {
        when(repo.findBySurveyid(anyLong())).thenReturn(null);

        assertThrows(SetNotFoundException.class, () -> service.getSurveyById(1L));

        verify(repo, times(1)).findBySurveyid(anyLong());
        verify(client, never()).getSet(anyLong());
    }

    @Test
    public void testAddEmails_Success() {
        // Setup mock survey
        when(repo.findBySurveyid(anyLong())).thenReturn(survey);

        // Mock saving of emails
        List<Emails> mockedSavedEmails = Arrays.asList(
                new Emails(1L, "email1@example.com", 1L, Status.PENDING),
                new Emails(2L, "email2@example.com", 1L, Status.PENDING)
        );
        when(emailRepository.saveAll(anyList())).thenReturn(mockedSavedEmails);

        // Call the method with 2 emails
        List<String> emailsToAdd = Arrays.asList("email1@example.com", "email2@example.com");
        List<Emails> emails = service.addEmails(1L, emailsToAdd);

        // Assert that 2 emails were returned
        assertNotNull(emails);
        assertEquals(2, emails.size());  // Expecting 2 emails

        // Additional debug output to verify what's being returned
        emails.forEach(email -> System.out.println("Saved Email: " + email.getEmail()));

        verify(repo, times(1)).findBySurveyid(anyLong());
        verify(emailRepository, times(1)).saveAll(anyList());
    }


    @Test
    public void testAddEmails_SetNotFoundException() {
        when(repo.findBySurveyid(anyLong())).thenThrow(new SetNotFoundException("Invalid email found."));

        assertThrows(SetNotFoundException.class, () -> service.addEmails(1L, Arrays.asList("email1@example.com")));

        verify(repo, times(1)).findBySurveyid(anyLong());
        verify(emailRepository, never()).saveAll(anyList());
    }

    @Test
    public void testGetEmails_Success() {
        when(emailRepository.findBySurveyid(anyLong())).thenReturn(Collections.singletonList(new Emails()));

        List<Emails> emails = service.getEmails(1L);

        assertNotNull(emails);
        assertEquals(1, emails.size());

        verify(emailRepository, times(1)).findBySurveyid(anyLong());
    }

    @Test
    public void testGetEmails_SetNotFoundException() {
        // Mock repository to throw an exception to simulate a failure scenario
        when(emailRepository.findBySurveyid(anyLong())).thenThrow(new RuntimeException("Database error"));

        // Assert that a SetNotFoundException is thrown when the repository fails
        assertThrows(SetNotFoundException.class, () -> service.getEmails(1L),
                "Expected SetNotFoundException to be thrown, but nothing was thrown.");

        // Verify that the repository method was called once
        verify(emailRepository, times(1)).findBySurveyid(anyLong());
    }


}
