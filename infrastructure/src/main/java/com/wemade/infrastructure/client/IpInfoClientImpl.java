package com.wemade.infrastructure.client;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wemade.core.domain.AnalysisResult.IpStat;
import com.wemade.core.port.IpInfoClient;

@Component
public class IpInfoClientImpl implements IpInfoClient {

    private final RestClient restClient;

    public IpInfoClientImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    private final Cache<String, Map> ipCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public IpStat enrichIpInfo(IpStat ipStat) {
        return null;
    }
}
