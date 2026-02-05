package com.wemade.api.controller;

import com.wemade.api.dto.AnalysisIdResponse;
import com.wemade.api.dto.AnalysisResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Log Analysis", description = "대용량 로그 분석 및 통계 조회 API")
public interface LogAnalysisApiDocs {

    @Operation(
            summary = "로그 파일 업로드 및 분석 요청",
            description = """
                    로그 파일(.log, .txt)을 업로드하여 분석을 시작합니다.
                    <br>내부적으로 스트리밍 파싱을 수행하며, 분석이 완료되면 **분석 ID(analysisId)**를 반환합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "분석 성공 및 저장 완료",
                    content = @Content(schema = @Schema(implementation = AnalysisIdResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 파일 형식 또는 비어있는 파일",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류 (파일 처리 실패 등)",
                    content = @Content
            )
    })
    ResponseEntity<AnalysisIdResponse> uploadAndAnalyze(
            @Parameter(
                    description = "업로드할 로그 파일",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            MultipartFile file
    ) throws IOException;


    @Operation(
            summary = "분석 결과 상세 조회",
            description = """
                    분석 ID를 통해 통계 결과를 조회합니다.
                    <br>**size** 파라미터를 조절하여 상위 몇 개의 데이터를 볼지 결정할 수 있습니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = AnalysisResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 ID의 분석 결과를 찾을 수 없음",
                    content = @Content
            )
    })
    ResponseEntity<AnalysisResponse> getResult(
            @Parameter(description = "분석 ID", example = "1", required = true)
            Long id,

            @Parameter(description = "상위 N개 데이터 조회 크기 (Top URL, IP 등)", example = "5", required = true)
            int size
    );
}