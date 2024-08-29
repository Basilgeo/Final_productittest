package com.example.AssessmentService.Controller;

import com.example.AssessmentService.controller.AssessmentController;
import com.example.AssessmentService.dto.AnswerDTO;
import com.example.AssessmentService.dto.AssessmentDTO;
import com.example.AssessmentService.exception.ResourceNotFoundException;
import com.example.AssessmentService.model.Assessment;
import com.example.AssessmentService.model.Question;
import com.example.AssessmentService.service.AssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AssessmentControllerTest {

    @Mock
    private AssessmentService assessmentService;

    @InjectMocks
    private AssessmentController assessmentController;

    private AssessmentDTO assessmentDTO;
    private Assessment assessment;
    private Question question;
    private List<AnswerDTO> answerDTOs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock data
        question = new Question();
        question.setQuestionId(1L);
        question.setDescription("Sample Question");

        List<Question> questions = new ArrayList<>();
        questions.add(question);

        assessmentDTO = new AssessmentDTO();
        assessmentDTO.setSetName("Test Set");
        assessmentDTO.setDomain("Test Domain");

        assessment = new Assessment();
        assessment.setSetid(1L);
        assessment.setSetName("Test Set");
        assessment.setQuestions(questions);

        answerDTOs = new ArrayList<>();
        answerDTOs.add(new AnswerDTO("Answer 1", "Suggestion 1"));
    }

    @Test
    void testGetAllAssessments() {
        List<Assessment> assessments = Arrays.asList(assessment);
        when(assessmentService.getAllAssessments()).thenReturn(assessments);

        ResponseEntity<List<Assessment>> response = assessmentController.getAllAssessments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(assessmentService, times(1)).getAllAssessments();
    }

    @Test
    void testCreateAssessment() {
        when(assessmentService.createAssessment(assessmentDTO)).thenReturn(assessment);

        ResponseEntity<?> response = assessmentController.createAssessment(assessmentDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(assessmentService, times(1)).createAssessment(assessmentDTO);
    }

    @Test
    void testGetQuestionsBySetName() {
        when(assessmentService.getQuestionsSetName("Test Set")).thenReturn(assessment.getQuestions());

        ResponseEntity<List<Question>> response = assessmentController.getQuestionsBySetName("Test Set");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(assessmentService, times(1)).getQuestionsSetName("Test Set");
    }

    @Test
    void testGetQuestionsBySetId() {
        when(assessmentService.getQuestionsSetId(1L)).thenReturn(assessment.getQuestions());

        ResponseEntity<List<Question>> response = assessmentController.getQuestionsBySetid(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(assessmentService, times(1)).getQuestionsSetId(1L);
    }

    @Test
    void testUpdateQuestion() {
        when(assessmentService.updateQuestion(1L, 1L, answerDTOs)).thenReturn("Question updated successfully");

        ResponseEntity<String> response = assessmentController.updateQuestion(1L, 1L, answerDTOs);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Question updated successfully", response.getBody());
        verify(assessmentService, times(1)).updateQuestion(1L, 1L, answerDTOs);
    }

    @Test
    void testDeleteQuestion() {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("message", "Question deleted successfully");
        when(assessmentService.deleteQuestion(1L, 1L)).thenReturn(responseMap);

        ResponseEntity<Map<String, String>> response = assessmentController.deleteQuestion(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Question deleted successfully", response.getBody().get("message"));
        verify(assessmentService, times(1)).deleteQuestion(1L, 1L);
    }

    @Test
    void testFetchQuestion() {
        when(assessmentService.fetchques(1L)).thenReturn(Optional.of(question));

        Optional<Question> response = assessmentController.fetchques(1L);

        assertNotNull(response);
        assertEquals("Sample Question", response.get().getDescription());
        verify(assessmentService, times(1)).fetchques(1L);
    }

    @Test
    void testHandleResourceNotFoundException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");

        ResponseEntity<String> response = assessmentController.handleResourceNotFoundException(exception);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Resource not found", response.getBody());
    }

    @Test
    void testHandleDataIntegrityException() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Data integrity violation");

        ResponseEntity<String> response = assessmentController.handleDataIntegrityException(exception);

        assertEquals("set alredy exists", response.getBody());  // Use the actual string with the typo
    }
}
