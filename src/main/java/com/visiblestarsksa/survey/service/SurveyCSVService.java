package com.visiblestarsksa.survey.service;

import com.visiblestarsksa.survey.exception.FormatException;
import com.visiblestarsksa.survey.helpers.SurveyCSVHelper;
import com.visiblestarsksa.survey.models.ECategory;
import com.visiblestarsksa.survey.models.Survey;
import com.visiblestarsksa.survey.repository.SurveyRepository;
import com.visiblestarsksa.survey.util.EnumUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class SurveyCSVService {
    @Autowired SurveyRepository repository;

    public void save(
            MultipartFile questions,
            MultipartFile users,
            String title_en,
            String title_ar,
            String category) {
        try {
            repository.save(
                    Survey.builder()
                            .title_en(title_en)
                            .title_ar(title_ar)
                            .category(EnumUtil.value(ECategory.class, category, ECategory.RETAIL))
                            .questions(SurveyCSVHelper.csvToQuestions(questions.getInputStream()))
                            .users(SurveyCSVHelper.csvToSurveyUsers(users.getInputStream()))
                            .build());
        } catch (IOException | FormatException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public void update(
            Survey survey,
            MultipartFile questions,
            MultipartFile users,
            String title_en,
            String title_ar,
            String category) {
        try {
            repository.save(
                    Survey.builder()
                            .id(survey.getId())
                            .title_en(title_en)
                            .title_ar(title_ar)
                            .category(EnumUtil.value(ECategory.class, category, null))
                            .questions(SurveyCSVHelper.csvToQuestions(questions.getInputStream()))
                            .users(SurveyCSVHelper.csvToSurveyUsers(users.getInputStream()))
                            .build());
        } catch (IOException | FormatException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load(Long id) {
        Optional<Survey> survey = repository.findById(id);
        ByteArrayInputStream in = SurveyCSVHelper.surveyToCSV(survey.get());
        return in;
    }

    public Optional<Survey> getSurvey(Long id) {
        return repository.findById(id);
    }

    public void deleteSurvey(Long id) {
        repository.deleteById(id);
    }
}
