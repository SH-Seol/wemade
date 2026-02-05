package com.wemade.core.domain;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

import com.wemade.core.domain.AnalysisResult.IpStat;
import com.wemade.core.domain.AnalysisResult.PathStat;
import com.wemade.core.domain.AnalysisResult.StatusCodeStat;

public class LogStatisticsAggregator {

    private static final int SAFETY_LIMIT = 1000;

    private final LocalDateTime startTime = LocalDateTime.now();
    private final LongAdder adder = new LongAdder();

    private final Map<String, LongAdder> ipCounts = new ConcurrentHashMap<>();
    private final Map<String, LongAdder> pathCounts = new ConcurrentHashMap<>();
    private final Map<Integer, LongAdder> statusCounts = new ConcurrentHashMap<>();

    public void process(LogEntry logEntry) {
        adder.increment();

        ipCounts.computeIfAbsent(logEntry.ip(), k -> new LongAdder()).increment();

        String cleanPath = normalizePath(logEntry.url());
        pathCounts.computeIfAbsent(cleanPath, k -> new LongAdder()).increment();
        statusCounts.computeIfAbsent(logEntry.status(), k -> new LongAdder()).increment();
    }

    public AnalysisResult createResult(Long id, long errorCount){
        long total = adder.sum();

        return new AnalysisResult(
                id,
                startTime,
                total,
                errorCount,
                calculateStatusRatio(total),
                getCollectedPaths(),
                getCollectedStatusCodes(),
                getCollectedIps()
        );
    }

    private String normalizePath(String path) {
        int queryIndex = path.indexOf('?');
        return (queryIndex > 0) ? path.substring(0, queryIndex) : path;
    }

    private Map<String, Double> calculateStatusRatio(long total){
        if(total == 0){
            return Collections.emptyMap();
        }
        Map<String, Long> groupCounts = new HashMap<>();

        statusCounts.forEach((code, add) -> {
            String groupKey = code / 100 + "xx";
            groupCounts.merge(groupKey, add.sum(), Long::sum);
        });

        return groupCounts.entrySet()
                .stream()
                .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Math.round((e.getValue() * 100.0 / total) * 10.0) / 10.0
        ));
    }

    private List<IpStat> getCollectedIps(){
        return ipCounts.entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue().sum(), e1.getValue().sum()))
                .limit(SAFETY_LIMIT)
                .map(e -> new IpStat(
                        e.getKey(),
                        e.getValue().sum(),
                        null, null, null, null)) // 정보는 비워둠 (조회 시 채움)
                .toList();
    }

    private List<PathStat> getCollectedPaths(){
        return pathCounts.entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue().sum(), e1.getValue().sum()))
                .limit(SAFETY_LIMIT)
                .map(e -> new PathStat("ALL" , e.getKey(), e.getValue().sum()))
                .toList();
    }

    private List<StatusCodeStat> getCollectedStatusCodes(){
        return statusCounts.entrySet()
                .stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue().sum(), e1.getValue().sum()))
                .limit(SAFETY_LIMIT)
                .map(e -> new StatusCodeStat(e.getKey(), e.getValue().sum()))
                .toList();
    }
}
