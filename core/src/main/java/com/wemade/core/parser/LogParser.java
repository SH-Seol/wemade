package com.wemade.core.parser;

import java.io.InputStream;
import java.util.function.Consumer;

import com.wemade.core.domain.analysis.LogEntry;

public interface LogParser {
    LogParsingResult parse(InputStream inputStream, Consumer<LogEntry> consumer);
}
