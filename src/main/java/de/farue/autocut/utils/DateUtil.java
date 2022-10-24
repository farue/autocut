package de.farue.autocut.utils;

import de.farue.autocut.domain.enumeration.SemesterTerms;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;

public class DateUtil {

    public static final LocalDate MIN_LOCAL_DATE = LocalDate.of(1970, 1, 1);
    public static final LocalDate MAX_LOCAL_DATE = LocalDate.of(2999, 12, 31);
    public static final Instant MIN_INSTANT = MIN_LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
    public static final Instant MAX_INSTANT = MAX_LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();

    public static LocalDate min(LocalDate... dates) {
        return Arrays.stream(dates).min(LocalDate::compareTo).orElseThrow();
    }

    public static LocalDate max(LocalDate... dates) {
        return Arrays.stream(dates).max(LocalDate::compareTo).orElseThrow();
    }

    public static Instant roundDown(Instant instant, TemporalUnit unit) {
        if (instant == null) {
            return null;
        }
        return instant.truncatedTo(unit);
    }

    public static Instant roundUp(Instant instant, TemporalUnit unit) {
        if (instant == null) {
            return null;
        }
        Instant roundedDown = roundDown(instant, unit);
        if (!roundedDown.equals(instant)) {
            return roundedDown.plus(1, unit);
        }
        return roundedDown;
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
