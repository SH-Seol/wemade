package com.wemade.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.wemade.core.domain.AnalysisResult;
import com.wemade.core.domain.AnalysisResult.IpStat;

public record AnalysisResponse(
        Long id,
        LocalDateTime analyzedAt,
        long totalRequests,
        long errorLineCount,
        Map<String, Double> statusRatio,
        List<AnalysisResult.PathStat> topPaths,
        List<AnalysisResult.StatusCodeStat> topStatusCodes,
        List<IpStat> topIps
) {
    public static AnalysisResponse from(AnalysisResult result) {
        return new AnalysisResponse(
                result.id(),
                result.analyzedAt(),
                result.totalRequests(),
                result.errorLineCount(),
                result.statusRatio(),
                result.topPaths(),
                result.topStatusCodes(),
                result.topIps()
        );
    }
}
