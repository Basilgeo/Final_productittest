package com.example.AssessmentService.Service;


import com.example.AssessmentService.dto.AnswerDTO;
import com.example.AssessmentService.dto.AssessmentDTO;
import com.example.AssessmentService.exception.ResourceNotFoundException;
import com.example.AssessmentService.model.Answer;
import com.example.AssessmentService.model.Assessment;
import com.example.AssessmentService.model.Question;
import com.example.AssessmentService.repo.AssessmentRepository;
import com.example.AssessmentService.repo.QuestionRepository;
import com.example.AssessmentService.service.AssessmentService;
import com.example.AssessmentService.utils.AssessmentUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssessmentServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AssessmentUtil assessmentUtil;

    @InjectMocks
    private AssessmentService assessmentService;

    private AssessmentDTO assessmentDTO;
    private Assessment assessment;
    private Question question;
    private AnswerDTO answerDTO;
    private List<AnswerDTO> answerDTOs;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup mock data
        answerDTO = new AnswerDTO("Answer 1", "Suggestion 1");
        answerDTOs = new ArrayList<>();
        answerDTOs.add(answerDTO);

        question = new Question();
        question.setQuestionId(1L);
        question.setDescription("Sample Question");

        List<Question> questions = new ArrayList<>();
        questions.add(question);

        assessmentDTO = new AssessmentDTO();
        assessmentDTO.setSetName("Test Set");
        assessmentDTO.setDomain("Test Domain");
        assessmentDTO.setQuestions(new ArrayList<>());

        assessment = new Assessment();
        assessment.setSetid(1L);
        assessment.setSetName("Test Set");
        assessment.setQuestions(questions);
    }

    @Test
    void testCreateAssessment() {
        when(assessmentUtil.MapToAssessment(assessmentDTO)).thenReturn(assessment);
        when(assessmentRepository.save(assessment)).thenReturn(assessment);

        Assessment createdAssessment = assessmentService.createAssessment(assessmentDTO);

        assertNotNull(createdAssessment);
        assertEquals("Test Set", createdAssessment.getSetName());
        verify(assessmentRepository, times(1)).save(assessment);
    }

    @Test
    void testUpdateQuestion_Success() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(assessment));
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        String result = assessmentService.updateQuestion(1L, 1L, answerDTOs);

        assertEquals("Question updated successfully", result);
        verify(assessmentRepository, times(1)).findById(1L);
        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    void testUpdateQuestion_AssessmentNotFound() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                assessmentService.updateQuestion(1L, 1L, answerDTOs));

        assertEquals("Set name is invalid", exception.getMessage());
        verify(assessmentRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteQuestion_Success() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(assessment));

        String message = assessmentService.deleteQuestion(1L, 1L).get("message");

        assertEquals("Question deleted successfully", message);
        verify(questionRepository, times(1)).deleteById(1L);
        verify(assessmentRepository, times(1)).save(any(Assessment.class));
    }

    @Test
    void testDeleteQuestion_AssessmentNotFound() {
        when(assessmentRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                assessmentService.deleteQuestion(1L, 1L));

        assertEquals("Set name is invalid", exception.getMessage());
        verify(assessmentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetQuestionsBySetName_Success() {
        when(assessmentRepository.findBySetName("Test Set")).thenReturn(Optional.of(assessment));

        List<Question> questions = assessmentService.getQuestionsSetName("Test Set");

        assertNotNull(questions);
        assertEquals(1, questions.size());
        verify(assessmentRepository, times(1)).findBySetName("Test Set");
    }

    @Test
    void testGetQuestionsBySetName_SetNotFound() {
        when(assessmentRepository.findBySetName("Invalid Set")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                assessmentService.getQuestionsSetName("Invalid Set"));

        assertEquals("set name is invalid", exception.getMessage());
        verify(assessmentRepository, times(1)).findBySetName("Invalid Set");
    }

    @Test
    void testGetQuestionsBySetId_Success() {
        when(assessmentRepository.findBySetid(1L)).thenReturn(Optional.of(assessment));

        List<Question> questions = assessmentService.getQuestionsSetId(1L);

        assertNotNull(questions);
        assertEquals(1, questions.size());
        verify(assessmentRepository, times(1)).findBySetid(1L);
    }

    @Test
    void testGetQuestionsBySetId_SetNotFound() {
        when(assessmentRepository.findBySetid(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                assessmentService.getQuestionsSetId(1L));

        assertEquals("set id is invalid", exception.getMessage());
        verify(assessmentRepository, times(1)).findBySetid(1L);
    }
}
