package com.wemade.core.domain;

import java.time.LocalDateTime;

public record LogEntry(
        String ip,
        String method,
        String url,
        int status,
        LocalDateTime timestamp
) {
}
