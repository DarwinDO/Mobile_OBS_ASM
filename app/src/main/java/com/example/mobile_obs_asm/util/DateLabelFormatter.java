package com.example.mobile_obs_asm.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class DateLabelFormatter {

    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.ENGLISH);

    private DateLabelFormatter() {
    }

    public static String formatIsoDateTime(String rawValue) {
        if (rawValue == null || rawValue.isEmpty()) {
            return "";
        }
        try {
            return LocalDateTime.parse(rawValue).format(OUTPUT_FORMATTER);
        } catch (DateTimeParseException exception) {
            return rawValue;
        }
    }
}
