package com.tracking.app.event;

import static java.util.Objects.requireNonNull;

import java.time.Instant;

public class LogEvent {

    private final Instant timestamp;
    private final String url;
    private final String userId;

    public LogEvent(final Instant timestamp, final String url, final String userId) {
        this.timestamp = requireNonNull(timestamp);
        this.url = requireNonNull(url);
        this.userId = requireNonNull(userId);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getUrl() {
        return url;
    }

    public String getUserId() {
        return userId;
    }
}
