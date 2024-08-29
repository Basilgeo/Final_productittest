package com.ust.Survey_api.repository;

import com.ust.Survey_api.model.Emails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<Emails,Long> {
    List<Emails> findBySurveyid(Long surveyid);
}
