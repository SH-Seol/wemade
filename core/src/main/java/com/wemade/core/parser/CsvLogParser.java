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
            throw new CoreException(CoreErrorType.FILE_READ_ERROR, "input stream 읽기 에러 발생: " + e.getMessage());
        }

        log.info("Parsing 완료. Total: {}, Success: {}, Error: {}", totalLines, successCount, errorCount);
        return new LogParsingResult(totalLines, successCount, errorCount, errorSamples);
    }

    private LogEntry parseLine(String line) {
        String[] parts = parseCsvLine(line);

        if (parts.length < 6) {
            throw new CoreException(CoreErrorType.INVALID_LOG_FORMAT, "Column 숫자 에러");
        }

        String timestampStr = parts[0];
        String ip = parts[1];
        String method = parts[2].toUpperCase();
        String url = parts[3];
        int status = Integer.parseInt(parts[5]);

        LocalDateTime timestamp = LocalDateTime.parse(timestampStr, DATE_FORMATTER);

        return new LogEntry(ip, method, url, status, timestamp);
    }

    private String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            // 1. 따옴표를 만난 경우 상태(inQuotes) 변경
            if (c == '\"') {
                inQuotes = !inQuotes;
            }
            // 2. 쉼표를 만났는데 따옴표 밖인 경우 토큰 완성
            else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0); // 버퍼 초기화
            }
            // 3. 그 외 문자: 버퍼에 담기
            else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());

        return tokens.toArray(new String[0]);
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