package com.visiblestarsksa.survey.helpers;

import com.visiblestarsksa.survey.models.Answer;
import com.visiblestarsksa.survey.models.EQuestionType;
import com.visiblestarsksa.survey.models.Question;
import com.visiblestarsksa.survey.models.Survey;
import com.visiblestarsksa.survey.models.SurveyUser;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SurveyCSVHelper {
    public static String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static boolean hasAnswerRecord(CSVRecord csvRecord, int i) {
        return (!StringUtils.isEmpty(getStringRecord(csvRecord, "answer_en_" + i))
                && !StringUtils.isEmpty(getStringRecord(csvRecord, "answer_ar_" + i)));
    }

    public static String getStringRecord(CSVRecord csvRecord, String name) {
        return csvRecord.isMapped(name) ? csvRecord.get(name) : "";
    }

    public static Integer getIntegerRecord(CSVRecord csvRecord, String name) {
        try {
            return csvRecord.isMapped(name) ? Integer.valueOf(csvRecord.get(name)) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static Long getLongRecord(CSVRecord csvRecord, String name) {
        try {
            return csvRecord.isMapped(name) ? Long.valueOf(csvRecord.get(name)) : -1L;
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    public static Boolean getBooleanRecord(CSVRecord csvRecord, String name) {
        return csvRecord.isMapped(name) ? Boolean.valueOf(csvRecord.get(name)) : false;
    }

    public static <E extends Enum<E>> E getEnumRecord(
            CSVRecord csvRecord, Class<E> clz, String name, E defaultValue) {
        return csvRecord.isMapped(name)
                ? EnumUtil.value(clz, csvRecord.get(name), defaultValue)
                : defaultValue;
    }

    public static Date getDateRecord(CSVRecord csvRecord, String name) {
        try {
            return csvRecord.isMapped(name)
                    ? new SimpleDateFormat("YYYY-mm-dd").parse(csvRecord.get(name))
                    : new Date();
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Set<Question> csvToQuestions(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser =
                        new CSVParser(
                                fileReader,
                                CSVFormat.DEFAULT
                                        .withFirstRecordAsHeader()
                                        .withIgnoreHeaderCase()
                                        .withTrim()); ) {

            Set<Question> questions = new HashSet<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                Set<Answer> answers = new HashSet<>();
                int i = 1;
                while (hasAnswerRecord(csvRecord, i)) {
                    answers.add(
                            Answer.builder()
                                    .answer_en(getStringRecord(csvRecord, "answer_en_" + i))
                                    .answer_ar(getStringRecord(csvRecord, "answer_ar_" + i))
                                    .build());
                    i++;
                }
                questions.add(
                        Question.builder()
                                .step_no(getIntegerRecord(csvRecord, "step_no"))
                                .question_en(getStringRecord(csvRecord, "question_en"))
                                .question_ar(getStringRecord(csvRecord, "question_ar"))
                                .required(getBooleanRecord(csvRecord, "required"))
                                .type(
                                        getEnumRecord(
                                                csvRecord,
                                                EQuestionType.class,
                                                "type",
                                                EQuestionType.LABEL))
                                .answers(answers)
                                .build());
            }
            return questions;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static Set<SurveyUser> csvToSurveyUsers(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                CSVParser csvParser =
                        new CSVParser(
                                fileReader,
                                CSVFormat.DEFAULT
                                        .withFirstRecordAsHeader()
                                        .withIgnoreHeaderCase()
                                        .withTrim()); ) {

            Set<SurveyUser> surveyUsers = new HashSet<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {

                surveyUsers.add(
                        SurveyUser.builder()
                                .extraction_date(getDateRecord(csvRecord, "extraction_date"))
                                .branch_code(getLongRecord(csvRecord, "branch_code"))
                                .mask_party(getLongRecord(csvRecord, "mask_party"))
                                .served_by(getStringRecord(csvRecord, "served_by"))
                                .trans_desc(getStringRecord(csvRecord, "trans_desc"))
                                .segment(getStringRecord(csvRecord, "segment"))
                                .gender(getStringRecord(csvRecord, "gender"))
                                .build());
            }

            return surveyUsers;
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
