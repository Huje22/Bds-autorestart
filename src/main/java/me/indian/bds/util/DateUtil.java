package me.indian.bds.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DateUtil {

    public static final ZoneId POLISH_ZONE = ZoneId.of("Europe/Warsaw");
    private static final Map<Character, String> UNIT_MAP = new HashMap<>();

    private DateUtil() {
    }

    public static String getFixedDate() {
        final LocalDateTime now = LocalDateTime.now(POLISH_ZONE);
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).replace(":", "-");
    }

    public static String getDate() {
        final LocalDateTime now = LocalDateTime.now(POLISH_ZONE);
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getTimeHM() {
        final LocalDateTime now = LocalDateTime.now(POLISH_ZONE);
        return now.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String getTimeHMS() {
        final LocalDateTime now = LocalDateTime.now(POLISH_ZONE);
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static String getTimeHMSMS() {
        final LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("HH:mm:ss:SSS"));
    }

    public static long localDateToLong(final LocalDate localDate) {
        return localDate.toEpochDay();
    }

    public static LocalDate longToLocalDate(final long seconds) {
        return Instant.ofEpochSecond(seconds).atZone(POLISH_ZONE).toLocalDate();
    }

    public static long formatDays(final long millis) {
        final long totalSeconds = millis / 1000;
        final long totalMinutes = totalSeconds / 60;
        final long totalHours = totalMinutes / 60;

        return totalHours / 24;
    }

    public static long formatHours(final long millis) {
        final long totalSeconds = millis / 1000;
        final long totalMinutes = totalSeconds / 60;
        final long totalHours = totalMinutes / 60;

        return totalHours % 24;
    }

    public static long formatMinutes(final long millis) {
        final long totalSeconds = millis / 1000;
        final long totalMinutes = totalSeconds / 60;

        return totalMinutes % 60;
    }

    public static long formatSeconds(final long millis) {
        return (millis / 1000) % 60;
    }

    public static String formatTime(final long millis, final List<Character> unitsPatern) {
        final StringBuilder formattedTime = new StringBuilder();
        final Map<Character, String> unitMap = getUnitMap(millis);

        for (final char unit : unitsPatern) {
            if (unitMap.containsKey(unit)) {
                formattedTime.append(unitMap.get(unit)).append(" ");
            }
        }

        return formattedTime.toString().trim();
    }

    private static Map<Character, String> getUnitMap(final long millis) {
        UNIT_MAP.clear();
        UNIT_MAP.put('d', formatDays(millis) + " dni");
        UNIT_MAP.put('h', formatHours(millis) + " godzin");
        UNIT_MAP.put('m', formatMinutes(millis) + " minut");
        UNIT_MAP.put('s', formatSeconds(millis) + " sekund");
        UNIT_MAP.put('i', millis % 1000 + " milisekund");
        return UNIT_MAP;
    }
}
