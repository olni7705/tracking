package com.tracking.app.event;

import static java.lang.String.format;

import com.tracking.app.exception.InvalidLogContentException;
import com.tracking.app.util.InstantUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class LogEventFactory {

    private static final Logger LOG = Logger.getLogger(LogEventFactory.class.getName());

    private static final String HEADER = "|timestamp|url|userid|";
    private static final String DELIMITER = "\\|";

    public static List<LogEvent> createLogEventsFromFileInRange(
        String path,
        Instant min,
        Instant max
    ) throws InvalidLogContentException {
        final List<LogEvent> logEvents = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String record = br.readLine();

            if (!HEADER.equals(record)) {
                throw new InvalidLogContentException(format(
                    "The header %s in file %s does not comfort with what's expected: %s",
                    record,
                    path,
                    HEADER
                ));
            }

            while ((record = br.readLine()) != null) {
                createLogEventInRange(record, min, max).ifPresent(logEvents::add);
            }
        } catch (IOException e) {
            LOG.severe(format("Application failed to read the input, \n%s", e.getMessage()));
            throw new UncheckedIOException(e);
        }
        return logEvents;
    }

    private static Optional<LogEvent> createLogEventInRange(
        String record,
        Instant min,
        Instant max
    ) throws InvalidLogContentException {
        Optional<LogEvent> logEvent = Optional.empty();
        final String[] fields = record.split(DELIMITER);
        validateRecord(fields, record);

        final Instant timestamp = InstantUtils.newInstant(fields[1]);
        if (!timestamp.isBefore(min) && !timestamp.isAfter(max)) {
            logEvent = Optional.of(
                new LogEvent(
                    timestamp,
                    fields[2],
                    fields[3]
                )
            );
        }
        return logEvent;
    }

    private static void validateRecord(String[] fields, String record) throws InvalidLogContentException {
        if (fields.length != 4) {
            throw new InvalidLogContentException(format(
                "The record: %s contains the wrong amount of columns",
                record
            ));
        } else if (!fields[0].isEmpty()) {
            throw new InvalidLogContentException(format(
                "The record: %s does not comfort with expectations. First and last element should be empty.",
                record
            ));
        }
    }
}
