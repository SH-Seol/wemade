package com.wemade.core.port;

import com.wemade.core.domain.AnalysisResult.IpStat;

public interface IpInfoClient {
    IpStat enrichIpInfo(IpStat ipStat);
}
