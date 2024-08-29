package com.ust.Survey_api.repository;

import com.ust.Survey_api.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey,Long> {
  Survey findBySurveyid(Long surveyid);
}
