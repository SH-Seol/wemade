package com.wemade.core.parser;

import java.util.List;

public record LogParsingResult(
        long totalLines,
        long successCount,
        long errorCount,
        List<String> errorSamples
) {
    public static LogParsingResult empty() {
        return new LogParsingResult(0, 0, 0, List.of());
    }
}
