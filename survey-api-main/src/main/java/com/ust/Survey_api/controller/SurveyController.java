package com.ust.Survey_api.controller;

import com.ust.Survey_api.exception.SetNotFoundException;
import com.ust.Survey_api.feign.FullResponse;
import com.ust.Survey_api.feign.PostDto;
import com.ust.Survey_api.feign.SetNameDto;
import com.ust.Survey_api.feign.SurveyRequestDto;
import com.ust.Survey_api.model.Emails;
import com.ust.Survey_api.model.Survey;
import com.ust.Survey_api.repository.SurveyRepository;
import com.ust.Survey_api.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}, allowedHeaders = "*")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyRepository surveyRepository;

    @PostMapping("/survey")
    public ResponseEntity<PostDto> addSurvey(@RequestBody SurveyRequestDto survey) {
       PostDto surveyResponse = surveyService.addSurvey(survey);
        if (surveyResponse == null) {
            throw new SetNotFoundException("Set name not found.");
        }
        return ResponseEntity.ok(surveyResponse);
    }

    @GetMapping("/surveys")
    public ResponseEntity<List<FullResponse>> getSurveys() {
        return ResponseEntity.ok(surveyService.getSurveys());
    }

    @GetMapping("/survey/surveyId/{surveyid}")
    public ResponseEntity<?> getSurveyById(@PathVariable Long surveyid) {
        FullResponse surveyOptional = surveyService.getSurveyById(surveyid);
        if(surveyOptional == null){
            throw new SetNotFoundException("Invalid surveyId");
        }
        return ResponseEntity.ok(surveyService.getSurveyById(surveyid));
    }


    @PostMapping("/survey/{surveyid}/addEmails")
    public ResponseEntity<List<Emails>> addEmails(@PathVariable Long surveyid, @RequestBody List<String> emails) {
        Survey survey = surveyRepository.findBySurveyid(surveyid);
        if (survey == null) {
            throw new SetNotFoundException(" surveyId not found");
        }
        return ResponseEntity.ok(surveyService.addEmails(surveyid, emails));
    }

    @GetMapping("/survey/{surveyid}/emails")
    public ResponseEntity<List<Emails>> getEmails(@PathVariable Long surveyid) {
        Survey survey = surveyRepository.findBySurveyid(surveyid);
        if (survey == null) {
            throw new SetNotFoundException("surveyId not found");
        }
        return ResponseEntity.ok(surveyService.getEmails(surveyid));
    }


    @ExceptionHandler(SetNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> handleNotFoundException(SetNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.OK).body(ex.getMessage());
    }
}
