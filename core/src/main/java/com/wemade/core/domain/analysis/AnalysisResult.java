package com.wemade.core.domain.analysis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record AnalysisResult(
        Long id,
        LocalDateTime analyzedAt,
        long totalRequests,
        long errorLineCount,
        //상태 코드 비율
        Map<String, Double> statusRatio,
        List<PathStat> topPaths,
        List<StatusCodeStat> topStatusCodes,
        List<IpStat> topIps
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
    ){
        public IpStat with(String country, String region, String city, String asn) {
            return new IpStat(this.ip, this.count, country, region, city, asn);
        }
    }
}
