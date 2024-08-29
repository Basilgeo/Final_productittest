package com.ust.Survey_api.service;

import com.ust.Survey_api.exception.SetNotFoundException;
import com.ust.Survey_api.feign.*;
import com.ust.Survey_api.model.Emails;
import com.ust.Survey_api.model.Status;
import com.ust.Survey_api.model.Survey;
import com.ust.Survey_api.repository.EmailRepository;
import com.ust.Survey_api.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ServiceImpl  implements  SurveyService{

    private static final AtomicLong counter = new AtomicLong(0);
    private static LocalDate expireDate;
    @Autowired
    private AssessmentClient client;

    @Autowired
    private SurveyRepository repo;

    @Autowired
    private EmailRepository emailRepository;
    @Value("${survey.expire.time.days}")
    private int expireTimeDays;

    @Override
    public PostDto addSurvey(SurveyRequestDto survey) {
        PostDto fr = new PostDto();
        long id = counter.incrementAndGet();
        fr.setId(id);
        fr.setRequestor(survey.getRequestor());
        fr.setCreatedDate(LocalDate.now());
        fr.setSetId(survey.getSetid());
        fr.setCompanyName(survey.getCompanyName());
        List<SetNameDto> optionalSetData = null;
        try {
            optionalSetData = client.getSet(survey.getSetid()).getBody();
            fr.setSetdata(optionalSetData);
        } catch (Exception e) {
            throw new SetNotFoundException("Set not found.");
        }

        Survey s = new Survey();
//        s.setSurveyid(survey.getSurveyid());
        s.setRequestor(survey.getRequestor());
        s.setSetid(survey.getSetid());
        s.setCreatedDate(LocalDate.now());
        expireDate=(LocalDate.now().plus(expireTimeDays, ChronoUnit.DAYS));
        s.setCompanyName(survey.getCompanyName());
        Survey se= repo.save(s);
        fr.setSurveyid(se.getSurveyid());
        return fr;
    }

    @Override
    public List<FullResponse> getSurveys() {
        List<FullResponse> frs = new ArrayList<FullResponse>();
        List<Survey> surveys = null;
        try{
             surveys =  repo.findAll();
        }
        catch(Exception e){
            throw new SetNotFoundException("Invalid survey id");
        }
        for (Survey survey : surveys) {
            FullResponse fr = new FullResponse();
            fr.setId(survey.getId());
            fr.setSurveyid(survey.getSurveyid());
            fr.setRequestor(survey.getRequestor());
            fr.setSetId(survey.getSetid());
            fr.setCreatedDate(survey.getCreatedDate());
            fr.setExpireDate(expireDate);
            fr.setCompanyName(survey.getCompanyName());
            List<SetNameDto> dtos = client.getSet(survey.getSetid()).getBody();
            fr.setSetdata(dtos);
            frs.add(fr);
        }
        return frs;
    }

    @Override
    public FullResponse getSurveyById(Long surveyId) {
        FullResponse fr = new FullResponse();

        Survey survey = repo.findBySurveyid(surveyId);

        if(survey == null){
            throw new SetNotFoundException("Invalid survey id");
        }
        fr.setId(survey.getId());
        fr.setSurveyid(survey.getSurveyid());
        fr.setRequestor(survey.getRequestor());
        fr.setSetId(survey.getSetid());
        fr.setCompanyName(survey.getCompanyName());
        fr.setCreatedDate(survey.getCreatedDate());
        fr.setExpireDate(expireDate);
        List<SetNameDto> dtos = client.getSet(survey.getSetid()).getBody();
        fr.setSetdata(dtos);
        return fr;
    }

    @Override
    public List<Emails> addEmails(Long surveyid, List<String> emails) {
        try {
            List<Emails> emailList = new ArrayList<Emails>();
            for (String email : emails) {
                Emails e = new Emails();
                e.setEmail(email);
                e.setSurveyid(surveyid);
                e.setStatus(Status.PENDING);
                emailList.add(e);
            }
            Survey survey = repo.findBySurveyid(surveyid);
            survey.setEmails(emailList);
            repo.save(survey);
            return emailRepository.saveAll(emailList);
        }
        catch (Exception e) {
            throw new SetNotFoundException("Invalid email found.");
        }

    }

    @Override
    public List<Emails> getEmails(Long surveyid) {
        List<Emails> e = null;
        try{
            e = emailRepository.findBySurveyid(surveyid);

        }
        catch(Exception ex){
            throw new SetNotFoundException("No Emails Found for survey " + surveyid);
        }
        return e;
    }


}
