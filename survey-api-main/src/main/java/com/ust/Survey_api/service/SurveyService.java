package com.ust.Survey_api.service;

import com.ust.Survey_api.feign.FullResponse;
import com.ust.Survey_api.feign.PostDto;
import com.ust.Survey_api.feign.SurveyRequestDto;
import com.ust.Survey_api.model.Emails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SurveyService {

    PostDto addSurvey(SurveyRequestDto survey);

    List<FullResponse> getSurveys();

    FullResponse getSurveyById(Long surveyId);

    List<Emails> addEmails(Long surveyId, List<String> emails);

    List<Emails> getEmails(Long surveyId);



}
