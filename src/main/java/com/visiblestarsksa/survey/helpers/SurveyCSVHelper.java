package com.visiblestarsksa.survey.helpers;

import com.visiblestarsksa.survey.models.Answer;
import com.visiblestarsksa.survey.models.EQuestionType;
import com.visiblestarsksa.survey.models.Question;
import com.visiblestarsksa.survey.models.Survey;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SurveyCSVHelper {
    public static String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static boolean hasAnswerRecord(CSVRecord csvRecord, int i) {
        return (!StringUtils.isEmpty(getRecord(csvRecord, "answer_en_" + i))
                && !StringUtils.isEmpty(getRecord(csvRecord, "answer_ar_" + i)));
    }

    public static String getRecord(CSVRecord csvRecord, String name) {
        return csvRecord.isMapped(name) ? csvRecord.get(name) : "";
    }

    public static Integer getIntegerRecord(CSVRecord csvRecord, String name) {
        return csvRecord.isMapped(name) ? Integer.valueOf(csvRecord.get(name)) : 0;
    }

    public static Boolean getBooleanRecord(CSVRecord csvRecord, String name) {
        return csvRecord.isMapped(name) ? Boolean.valueOf(csvRecord.get(name)) : false;
    }

    public static Set<Question> csvToSurvey(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser =
                        new CSVParser(
                                fileReader,
                                CSVFormat.DEFAULT
                                        .withFirstRecordAsHeader()
                                        .withIgnoreHeaderCase()
                                        .withTrim()); ) {

            Set<Question> questions = new TreeSet<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {

                Set<Answer> answers = new TreeSet<>();
                int i = 1;
                while (hasAnswerRecord(csvRecord, i)) {
                    answers.add(
                            Answer.builder()
                                    .answer_en(getRecord(csvRecord, "answer_en_" + i))
                                    .answer_ar(getRecord(csvRecord, "answer_ar_" + i))
                                    .build());
                    i++;
                }
                questions.add(
                        Question.builder()
                                .step_no(getIntegerRecord(csvRecord, "step_no"))
                                .question_en(getRecord(csvRecord, "question_en"))
                                .question_ar(getRecord(csvRecord, "question_ar"))
                                .required(getBooleanRecord(csvRecord, "required"))
                                .type(
                                        Enum.valueOf(
                                                EQuestionType.class, getRecord(csvRecord, "type")))
                                .answers(answers)
                                .build());
            }

            return questions;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream surveyToCSV(Survey survey) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format); ) {
            List<String> data = null;
            csvPrinter.printRecord(data);
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}
