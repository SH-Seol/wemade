package com.wemade.infrastructure.error;

public enum InfraErrorType {
    IP_INFO_CONNECTION_FAIL(InfraErrorCode.IP_INFO001, "IP 정보 조회 API 연결 실패", InfraErrorLevel.WARN),
    IP_INFO_TIMEOUT(InfraErrorCode.IP_INFO002, "IP 정보 조회 응답 시간 초과", InfraErrorLevel.WARN),
    IP_INFO_INVALID_RESPONSE(InfraErrorCode.IP_INFO003, "IP 정보 응답 데이터 처리 실패", InfraErrorLevel.WARN),
    STORAGE_SAVE_FAIL(InfraErrorCode.STORAGE001, "분석 결과 저장소 저장 실패", InfraErrorLevel.ERROR);
    ;

    private final InfraErrorCode errorCode;
    private final String message;
    private final InfraErrorLevel errorLevel;


    InfraErrorType(InfraErrorCode errorCode, String message, InfraErrorLevel errorLevel) {
        this.errorCode = errorCode;
        this.message = message;
        this.errorLevel = errorLevel;
    }

    public InfraErrorCode getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public InfraErrorLevel getErrorLevel() {
        return errorLevel;
    }
}
