package com.wemade.core.domain;

import java.time.LocalDateTime;
import java.util.Map;

public record AnalysisResult(
        Long id,
        LocalDateTime analyzedAt,
        long totalRequests,
        long errorLineCount,
        //상태 코드 비율
        Map<String, Double> statusRatio

) {
    public record PathStat(
       String method,
       String path,
       long count
    ){}

    public record StatusCodeStat(
            int code,
            long count
    ){}

    public record IpStat(
            String ip,
            long count,
            String country,
            String region,
            String city,
            String asn
    ){}
}
