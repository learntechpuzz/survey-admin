package com.visiblestarsksa.survey.util;

import com.visiblestarsksa.survey.exception.FormatException;

import org.apache.commons.csv.CSVRecord;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVUtil {

    public static String extractString(String key, CSVRecord record) throws FormatException {
        if (!record.isMapped(key)) {
            throw new FormatException("Field not used but expected: " + key);
        }

        return record.get(key);
    }

    public static int extractInt(String key, CSVRecord record) throws FormatException {
        if (!record.isMapped(key)) {
            throw new FormatException("Field not used but expected: " + key);
        }

        try {
            return Integer.parseInt(record.get(key));
        } catch (NumberFormatException e) {
            throw new FormatException("Failed to parse field: " + key, e);
        }
    }

    public static double extractDouble(String key, CSVRecord record) throws FormatException {
        if (!record.isMapped(key)) {
            throw new FormatException("Field not used but expected: " + key);
        }

        try {
            return Double.parseDouble(record.get(key));
        } catch (NumberFormatException e) {
            throw new FormatException("Failed to parse field: " + key, e);
        }
    }

    public static long extractLong(String key, CSVRecord record) throws FormatException {
        if (!record.isMapped(key)) {
            throw new FormatException("Field not used but expected: " + key);
        }

        try {
            return Long.parseLong(record.get(key));
        } catch (NumberFormatException e) {
            throw new FormatException("Failed to parse field: " + key, e);
        }
    }

    public static boolean extractBoolean(String key, CSVRecord record) throws FormatException {
        if (!record.isMapped(key)) {
            throw new FormatException("Field not used but expected: " + key);
        }
        return Boolean.parseBoolean(key);
    }

    public static Date extractDate(String key, CSVRecord record, String pattern)
            throws FormatException {
        if (!record.isMapped(key)) {
            throw new FormatException("Field not used but expected: " + key);
        }
        try {
            return new SimpleDateFormat(pattern).parse(record.get(key));
        } catch (ParseException e) {
            throw new FormatException("Failed to parse field: " + key, e);
        }
    }

    public static <E extends Enum<E>> E extractEnum(
            String key, CSVRecord record, Class<E> clz, E defaultValue) throws FormatException {

        if (!record.isMapped(key)) {
            throw new FormatException("Field not used but expected: " + key);
        }
        try {
            return EnumUtil.value(clz, record.get(key), defaultValue);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new FormatException("Failed to parse field: " + key, e);
        }
    }
}
