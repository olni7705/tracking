package com.tracking.app.event;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import com.tracking.app.exception.InvalidLogContentException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LogEventManager {

    private static final Logger LOG = Logger.getLogger(LogEventManager.class.getName());
    private static final String OUTPUT_HEADER = "|url|page views|visitors|";
    private static final String DELIMITER = "|";

    private final List<LogEvent> logEvents = new ArrayList<>();
    private final String path;
    private final Instant fromTimestamp;
    private final Instant toTimestamp;

    public LogEventManager(String path, Instant fromTimestamp, Instant toTimestamp) {
        this.path = requireNonNull(path);
        this.fromTimestamp = requireNonNull(fromTimestamp);
        this.toTimestamp = requireNonNull(toTimestamp);
    }

    public void handleLogEvents() {
        try {
            logEvents.addAll(
                LogEventFactory.createLogEventsFromFileInRange(
                    path,
                    fromTimestamp,
                    toTimestamp
                )
            );
        } catch (InvalidLogContentException e) {
            LOG.severe(format("Wrong content in input file\n%s", e.getMessage()));
            throw new RuntimeException(e);
        }
    }

    public void writeOutput(String path) throws IOException {
        final Map<String, Long> pageViewsMap = logEvents.stream()
            .collect(
                Collectors.groupingBy(
                    LogEvent::getUrl,
                    Collectors.counting()
                )
            );
        final Map<String, Integer> visitorsMap = logEvents.stream()
            .collect(
                Collectors.groupingBy(
                    LogEvent::getUrl,
                    Collectors.collectingAndThen(
                        Collectors.mapping(LogEvent::getUserId, toSet()),
                        Set::size
                    )
                )
            );

        final String outputString = buildOutputString(pageViewsMap, visitorsMap);
        writeContentToFile(path, outputString);
    }

    private static String buildOutputString(Map<String, Long> pageViewsMap, Map<String, Integer> visitorsMap) {
        final StringBuilder s = new StringBuilder(format("%s\n", OUTPUT_HEADER));

        for (final Map.Entry<String, Long> entrySet : pageViewsMap.entrySet()) {
            final String url = entrySet.getKey();
            final Long pageViews = entrySet.getValue();
            final Integer visitors = visitorsMap.get(url);

            s.append(DELIMITER);
            s.append(url);
            s.append(DELIMITER);
            s.append(pageViews);
            s.append(DELIMITER);
            s.append(visitors);
            s.append(DELIMITER);
            s.append("\n");
        }

        return s.toString();
    }

    private static void writeContentToFile(String path, String content) throws IOException {

        final File file = new File(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        }
    }
}
