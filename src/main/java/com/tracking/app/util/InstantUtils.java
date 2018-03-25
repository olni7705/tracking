package com.tracking.app.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class InstantUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd HH:mm:ss'UTC'")
        .toFormatter()
        .withZone(ZoneId.of("UTC"));

    public static Instant newInstant(String timestamp) {
        return DATE_TIME_FORMATTER.parse(timestamp, Instant::from);
    }
}
