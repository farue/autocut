package de.farue.autocut.utils;

import java.time.LocalDate;
import java.util.Arrays;

import de.farue.autocut.domain.enumeration.SemesterTerms;

public class DateUtil {

    public static LocalDate min(LocalDate... dates) {
        return Arrays.stream(dates).min(LocalDate::compareTo).orElseThrow();
    }

    public static LocalDate max(LocalDate... dates) {
        return Arrays.stream(dates).max(LocalDate::compareTo).orElseThrow();
    }

    public static SemesterTerms semesterTermAt(int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month out of bounds: " + month);
        }

        if (month <= 3 || month >= 10) {
            return SemesterTerms.WINTER_TERM;
        } else {
            return SemesterTerms.SUMMER_TERM;
        }
    }
}
