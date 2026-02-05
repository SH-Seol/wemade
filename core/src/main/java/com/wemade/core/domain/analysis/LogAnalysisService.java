package com.wemade.core.domain.analysis;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wemade.core.domain.analysis.AnalysisResult.IpStat;
import com.wemade.core.domain.analysis.AnalysisResult.PathStat;
import com.wemade.core.domain.analysis.AnalysisResult.StatusCodeStat;
import com.wemade.core.error.CoreErrorType;
import com.wemade.core.error.CoreException;
import com.wemade.core.parser.LogParser;
import com.wemade.core.parser.LogParsingResult;
import com.wemade.core.port.IpInfoClient;

@Service
public class LogAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(LogAnalysisService.class);

    private final LogParser logParser;
    private final LogAnalysisRepository logAnalysisRepository;
    private final IpInfoClient ipInfoClient;

    private final AtomicLong sequence = new AtomicLong(1);

    public LogAnalysisService(LogParser logParser, LogAnalysisRepository logAnalysisRepository,
                              IpInfoClient ipInfoClient) {
        this.logParser = logParser;
        this.logAnalysisRepository = logAnalysisRepository;
        this.ipInfoClient = ipInfoClient;
    }

    public Long analyze(InputStream inputStream) {
        long startTime = System.currentTimeMillis();
        log.info("로그 분석 시작");

        LogStatisticsAggregator aggregator = new LogStatisticsAggregator();

        LogParsingResult parsingResult = logParser.parse(inputStream, aggregator::process);
        Long analysisId = sequence.getAndIncrement();
        AnalysisResult result = aggregator.createResult(analysisId, parsingResult.errorCount());

        long duration = System.currentTimeMillis() - startTime;

        log.info("분석 완료 - ID: {}, 소요시간: {}ms, 총 라인: {}, 파싱 에러: {}",
                analysisId, duration, result.totalRequests(), result.errorLineCount());

        return logAnalysisRepository.save(result);
    }

    public AnalysisResult getResult(Long id, int topN) {
        AnalysisResult result = logAnalysisRepository.findById(id)
                .orElseThrow(() -> new CoreException(CoreErrorType.ANALYSIS_RESULT_NOT_FOUND));

        List<PathStat> slicedPaths = result.topPaths().stream()
                .limit(topN)
                .toList();

        List<StatusCodeStat> slicedCodes = result.topStatusCodes().stream()
                .limit(topN)
                .toList();

        List<IpStat> enrichedIps = result.topIps().stream()
                .limit(topN)
                .map(ipInfoClient::enrichIpInfo)
                .toList();

        return new AnalysisResult(
                result.id(),
                result.analyzedAt(),
                result.totalRequests(),
                result.errorLineCount(),
                result.statusRatio(),
                slicedPaths,
                slicedCodes,
                enrichedIps
        );
    }
}
