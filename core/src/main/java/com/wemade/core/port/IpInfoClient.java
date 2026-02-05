package com.wemade.core.port;

import com.wemade.core.domain.analysis.AnalysisResult.IpStat;

public interface IpInfoClient {
    IpStat enrichIpInfo(IpStat ipStat);
}
