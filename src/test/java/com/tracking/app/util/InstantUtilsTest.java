package com.tracking.app.util;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.junit.Test;

public class InstantUtilsTest {

    @Test
    public void shouldParseInstant() {
        final String timestamp = "2013-09-01 11:00:00UTC";
        final Instant instant = InstantUtils.newInstant(timestamp);
        assertTrue(instant.toString().equals("2013-09-01T11:00:00Z"));
    }

    @Test(expected = DateTimeParseException.class)
    public void shouldOnlyParseUTC() {
        final String timestamp = "2013-09-01 12:00:00Europe/Paris";
        final Instant instant = InstantUtils.newInstant(timestamp);
        assertTrue(instant.toString().equals("2013-09-01T11:00:00Z"));
    }
}