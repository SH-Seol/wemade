package com.wemade.api.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wemade.api.dto.AnalysisIdResponse;
import com.wemade.api.dto.AnalysisResponse;
import com.wemade.core.domain.AnalysisResult;
import com.wemade.core.domain.LogAnalysisService;

@RestController
@RequestMapping("/api/v1/analysis")
public class LogAnalysisController implements LogAnalysisApiDocs{

    private final LogAnalysisService logAnalysisService;

    public LogAnalysisController(LogAnalysisService logAnalysisService) {
        this.logAnalysisService = logAnalysisService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AnalysisIdResponse> uploadAndAnalyze(
            @RequestPart("file") MultipartFile file
    ) throws IOException {

        Long analysisId = logAnalysisService.analyze(file.getInputStream());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AnalysisIdResponse(analysisId));
    }


    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResponse> getResult(@PathVariable("id") Long id, @RequestParam("size") int size) {
        AnalysisResult result = logAnalysisService.getResult(id, size);

        return ResponseEntity.ok(AnalysisResponse.from(result));
    }
}
