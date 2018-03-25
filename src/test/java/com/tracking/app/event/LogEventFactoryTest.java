package com.tracking.app.event;

import static org.junit.Assert.assertTrue;

import com.tracking.app.exception.InvalidLogContentException;
import com.tracking.app.util.InstantUtils;
import java.time.Instant;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;

public class LogEventFactoryTest {

    private static Instant before;
    private static Instant after;

    @BeforeClass
    public static void setUp() {
        final String timestampBefore = "2010-09-01 11:00:00UTC";
        final String timestampAfter = "2018-09-01 11:00:00UTC";
        before = InstantUtils.newInstant(timestampBefore);
        after = InstantUtils.newInstant(timestampAfter);
    }

    @Test
    public void shouldHandleHeader() throws Exception {
        final List<LogEvent> logEvents = LogEventFactory.createLogEventsFromFileInRange(
            getClass().getClassLoader().getResource("log_header.txt").getFile(),
            before,
            after
        );
        assertTrue(logEvents.isEmpty());
    }

    @Test
    public void shouldHandleRecords() throws Exception {
        final List<LogEvent> logEvents = LogEventFactory.createLogEventsFromFileInRange(
            getClass().getClassLoader().getResource("log_record.txt").getFile(),
            before,
            after
        );
        assertTrue(logEvents.size() == 1);
    }

    @Test(expected = InvalidLogContentException.class)
    public void shouldThrowForInvalidHeader() throws Exception {
        final List<LogEvent> logEvents = LogEventFactory.createLogEventsFromFileInRange(
            getClass().getClassLoader().getResource("log_header_wrong.txt").getFile(),
            before,
            after
        );
        assertTrue(logEvents.isEmpty());
    }
}