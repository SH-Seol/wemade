package com.wemade.infrastructure.client;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.wemade.core.domain.analysis.AnalysisResult.IpStat;
import com.wemade.core.port.IpInfoClient;
import com.wemade.infrastructure.error.InfraErrorType;
import com.wemade.infrastructure.error.InfraException;

@Component
public class IpInfoClientImpl implements IpInfoClient {

    @Value("${external.ipinfo.api_token}")
    private String apiToken;

    private static final Logger log = LoggerFactory.getLogger(IpInfoClientImpl.class);
    private final RestClient restClient;

    public IpInfoClientImpl(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://ipinfo.io")
                .build();
    }

    private final Cache<String, Map> ipCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public IpStat enrichIpInfo(IpStat ipStat) {
        String ip = ipStat.ip();

        try{
            Map cachedInfo = ipCache.getIfPresent(ip);
            if(cachedInfo != null) {
                return applyInfo(ipStat, cachedInfo);
            }

            String url = "/" + ip + "/json" + (apiToken == null || apiToken.isEmpty() ? "" : "?token=" + apiToken);

            Map response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(Map.class);

            if(response != null) {
                ipCache.put(ip, response);
                return applyInfo(ipStat, response);
            }
        }catch(Exception e) {
            InfraErrorType errorType;
            if(e instanceof SocketTimeoutException) {
                errorType = InfraErrorType.IP_INFO_TIMEOUT;
            }
            else{
                errorType = InfraErrorType.IP_INFO_CONNECTION_FAIL;
            }

            InfraException infraEx = new InfraException(errorType, "Target IP: " + ip, e);

            // 출력 예: [IF-102] IP 정보 조회 응답 시간 초과 - Target IP: 127.0.0.1
            log.warn("[{}] {} - {}",
                    infraEx.getErrorType().getErrorCode().getCode(),
                    infraEx.getErrorType().getMessage(),
                    infraEx.getMessage()
            );
        }

        return ipStat;
    }

    private IpStat applyInfo(IpStat origin, Map info) {
        String country = info.getOrDefault("country", "Unknown").toString();
        String region = info.getOrDefault("region", "Unknown").toString();
        String city = info.getOrDefault("city", "Unknown").toString();
        String org = info.getOrDefault("org", "Unknown").toString();

        return origin.with(country, region, city, org);
    }
}
