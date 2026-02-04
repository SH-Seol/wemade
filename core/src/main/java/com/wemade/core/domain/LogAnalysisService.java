package com.wemade.core.domain;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.wemade.core.error.CoreErrorType;
import com.wemade.core.error.CoreException;
import com.wemade.core.parser.LogParser;
import com.wemade.core.parser.LogParsingResult;

@Service
public class LogAnalysisService {

    private final LogParser logParser;
    private final LogAnalysisRepository logAnalysisRepository;

    private final AtomicLong sequence = new AtomicLong(1);

    public LogAnalysisService(LogParser logParser, LogAnalysisRepository logAnalysisRepository) {
        this.logParser = logParser;
        this.logAnalysisRepository = logAnalysisRepository;
    }

    public Long analyze(InputStream inputStream, int topN) {
        LogStatisticsAggregator aggregator = new LogStatisticsAggregator();

        LogParsingResult parsingResult = logParser.parse(inputStream, aggregator::process);
        Long analysisId = sequence.getAndIncrement();
        AnalysisResult result = aggregator.createResult(analysisId, parsingResult.errorCount(), topN);

        return logAnalysisRepository.save(result);
    }

    public AnalysisResult getResult(Long id) {
        return logAnalysisRepository.findById(id).orElseThrow(() -> new CoreException(CoreErrorType.ANALYSIS_RESULT_NOT_FOUND));
    }
}
