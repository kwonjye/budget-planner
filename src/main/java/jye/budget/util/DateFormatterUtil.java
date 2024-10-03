package jye.budget.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatterUtil {
    public static String formatLocalDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일(EEE)");
        return date.format(formatter);
    }
}
