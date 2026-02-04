package com.wemade.infrastructure.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.wemade.core.domain.AnalysisResult;
import com.wemade.core.domain.LogAnalysisRepository;

@Repository
public class InMemoryAnalysisRepository implements LogAnalysisRepository {

    private final Map<Long, AnalysisResult> storage = new ConcurrentHashMap<>();

    public Long save(AnalysisResult result){
        storage.put(result.id(), result);
        return result.id();
    }
    public Optional<AnalysisResult> findById(Long id){
        return Optional.ofNullable(storage.get(id));
    }
}
