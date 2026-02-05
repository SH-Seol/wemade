package com.wemade.core.domain.analysis;

import java.util.Optional;

public interface LogAnalysisRepository {
    Long save(AnalysisResult result);
    Optional<AnalysisResult> findById(Long id);
}
