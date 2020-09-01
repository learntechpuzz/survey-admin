package com.visiblestarsksa.survey.helpers;

import static com.visiblestarsksa.survey.util.CSVUtil.extractBoolean;
import static com.visiblestarsksa.survey.util.CSVUtil.extractDate;
import static com.visiblestarsksa.survey.util.CSVUtil.extractEnum;
import static com.visiblestarsksa.survey.util.CSVUtil.extractInt;
import static com.visiblestarsksa.survey.util.CSVUtil.extractLong;
import static com.visiblestarsksa.survey.util.CSVUtil.extractString;

import com.visiblestarsksa.survey.exception.FormatException;
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

    public static boolean hasAnswerRecord(CSVRecord record, int i) throws FormatException {
        return (record.isMapped("answer_en_" + i)
                && !StringUtils.isEmpty(extractString("answer_en_" + i, record))
                && record.isMapped("answer_ar_" + i)
                && !StringUtils.isEmpty(extractString("answer_ar_" + i, record)));
    }

    public static Set<Question> csvToQuestions(InputStream is) throws IOException, FormatException {
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
                                    .answer_en(extractString("answer_en_" + i, csvRecord))
                                    .answer_ar(extractString("answer_ar_" + i, csvRecord))
                                    .build());
                    i++;
                }
                questions.add(
                        Question.builder()
                                .step_no(extractInt("step_no", csvRecord))
                                .question_en(extractString("question_en", csvRecord))
                                .question_ar(extractString("question_ar", csvRecord))
                                .required(extractBoolean("required", csvRecord))
                                .type(extractEnum("type", csvRecord, EQuestionType.class, null))
                                .answers(answers)
                                .build());
            }
            return questions;
        }
    }

    public static Set<SurveyUser> csvToSurveyUsers(InputStream is)
            throws IOException, FormatException {
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
                                .extraction_date(
                                        extractDate("extraction_date", csvRecord, "yyyy-MM-dd"))
                                .branch_code(extractLong("branch_code", csvRecord))
                                .mask_party(extractLong("mask_party", csvRecord))
                                .served_by(extractString("served_by", csvRecord))
                                .trans_desc(extractString("trans_desc", csvRecord))
                                .segment(extractString("segment", csvRecord))
                                .gender(extractString("gender", csvRecord))
                                .build());
            }

            return surveyUsers;
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
