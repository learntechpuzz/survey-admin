package com.visiblestarsksa.survey.controllers;

import com.visiblestarsksa.survey.helpers.SurveyCSVHelper;
import com.visiblestarsksa.survey.models.Survey;
import com.visiblestarsksa.survey.payload.response.MessageResponse;
import com.visiblestarsksa.survey.service.SurveyCSVService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@CrossOrigin("*")
@Controller
@RequestMapping("/api/csv")
public class SurveyCSVController {

    @Autowired SurveyCSVService surveyCSVService;

    @PostMapping("/survey")
    public ResponseEntity<MessageResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title_en") String title_en,
            @RequestParam("title_ar") String title_ar,
            @RequestParam("category") String category) {
        String message = "";
        if (SurveyCSVHelper.hasCSVFormat(file)) {
            try {
                surveyCSVService.save(file, title_en, title_ar, category);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body(new MessageResponse(message));
            }
        }
        message = "Please upload a csv file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(message));
    }

    @GetMapping("/survey/{id}")
    public ResponseEntity<Survey> getSurvey(@PathVariable("id") long id) {
        try {
            Optional<Survey> survey = surveyCSVService.getSurvey(id);
            if (survey.isPresent()) {
                return new ResponseEntity<>(survey.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/survey/{id}")
    public ResponseEntity<MessageResponse> updateSurvey(
            @PathVariable("id") long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title_en") String title_en,
            @RequestParam("title_ar") String title_ar,
            @RequestParam("category") String category) {

        Optional<Survey> surveyData = surveyCSVService.getSurvey(id);
        if (surveyData.isPresent()) {
            String message = "";
            if (SurveyCSVHelper.hasCSVFormat(file)) {
                try {
                    surveyCSVService.update(surveyData.get(), file, title_en, title_ar, category);
                    message = "Uploaded the file successfully: " + file.getOriginalFilename();
                    return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse(message));
                } catch (Exception e) {
                    message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                            .body(new MessageResponse(message));
                }
            }
            message = "Please upload a csv file!";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(message));

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/survey/{id}")
    public ResponseEntity<HttpStatus> deleteSurvey(@PathVariable("id") long id) {
        try {
            surveyCSVService.deleteSurvey(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/survey/download/{id}")
    public ResponseEntity<Resource> downloadSurvey(@PathVariable("id") long id) {
        String filename = "survey.csv";
        InputStreamResource file = new InputStreamResource(surveyCSVService.load(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }
}
