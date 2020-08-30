package com.visiblestarsksa.survey.repository;

import com.visiblestarsksa.survey.models.Survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {}
