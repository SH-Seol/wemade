package com.wemade.core.parser;

import com.wemade.core.domain.LogEntry;
import com.wemade.core.error.CoreErrorLevel;
import com.wemade.core.error.CoreErrorType;
import com.wemade.core.error.CoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

@Component
public class CsvLogParser implements LogParser {

    private static final Logger log = LoggerFactory.getLogger(CsvLogParser.class);
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("M/d/yyyy, h:mm:ss.SSS a", Locale.ENGLISH);
    private static final String DELIMITER = ",";

    private static final int MIN_SPLIT_LENGTH = 7;
    private static final int MAX_ERROR_SAMPLES = 5;

    @Override
    public LogParsingResult parse(InputStream inputStream, Consumer<LogEntry> logConsumer) {
        long totalLines = 0;
        long successCount = 0;
        long errorCount = 0;
        List<String> errorSamples = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;

            // 1. 헤더 처리
            if ((line = reader.readLine()) != null) {
                if (!line.startsWith("TimeGenerated")) {
                    log.debug("헤더 스킵: {}", line);
                }
            }

            // 2. 라인 파싱
            while ((line = reader.readLine()) != null) {
                totalLines++;
                if (line.trim().isEmpty()) continue;

                try {
                    LogEntry entry = parseLine(line);
                    logConsumer.accept(entry);
                    successCount++;

                } catch (CoreException e) {
                    errorCount++;
                    handleParsingException(e, line, totalLines, errorSamples);
                } catch (Exception e) {
                    errorCount++;
                    CoreException wrappedEx = new CoreException(CoreErrorType.INVALID_LOG_FORMAT, e.getMessage());
                    handleParsingException(wrappedEx, line, totalLines, errorSamples);
                }
            }

        } catch (IOException e) {
            throw new CoreException(CoreErrorType.FILE_READ_ERROR, "Error reading input stream: " + e.getMessage());
        }

        log.info("Parsing completed. Total: {}, Success: {}, Error: {}", totalLines, successCount, errorCount);
        return new LogParsingResult(totalLines, successCount, errorCount, errorSamples);
    }

    private LogEntry parseLine(String line) {
        String[] parts = line.split(DELIMITER);

        // [핵심 수정] 날짜 내 콤마 때문에 length가 예상보다 1개 더 많아짐
        if (parts.length < MIN_SPLIT_LENGTH) {
            throw new CoreException(
                    CoreErrorType.INVALID_LOG_FORMAT,
                    "Column count too short: " + parts.length
            );
        }

        try {
            // Timestamp 복원 (parts[0] + "," + parts[1])
            String timestampStr = (parts[0] + "," + parts[1]).trim().replace("\"", "");

            String ip = parts[2].trim();
            String method = parts[3].trim().toUpperCase();
            String url = parts[4].trim();
            String statusStr = parts[6].trim();

            int status = Integer.parseInt(statusStr);
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DATE_FORMATTER);

            return new LogEntry(ip, method, url, status, timestamp);

        } catch (Exception e) {
            throw new CoreException(CoreErrorType.INVALID_LOG_FORMAT, "Data conversion failed: " + e.getMessage());
        }
    }

    private void handleParsingException(CoreException e, String line, long lineNumber, List<String> errorSamples) {
        if (errorSamples.size() < MAX_ERROR_SAMPLES) {
            errorSamples.add(line);
            if (e.getErrorType().getErrorLevel() == CoreErrorLevel.WARN) {
                log.warn("{} - Line: {}, Reason: {}, Data: {}",
                        e.getErrorType().getMessage(), lineNumber + 1, e.getMessage(), line);
            }
        }
    }
}