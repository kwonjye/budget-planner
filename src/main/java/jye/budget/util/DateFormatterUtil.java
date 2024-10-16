package jye.budget.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatterUtil {
    public static String formatLocalDateWithDay(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일(EEE)", Locale.KOREAN);
        return date.format(formatter);
    }
    public static String formatYearMonth(String yearMonthStr) {
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern("yyyy-MM"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월");
        return yearMonth.format(formatter);
    }
    public static String formatAnnual(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일");
        return date.format(formatter);
    }
    public static String formatMonthly(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d일");
        return date.format(formatter);
    }
}
