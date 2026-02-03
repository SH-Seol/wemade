package com.wemade.core.error;

public enum CoreErrorType {
    INVALID_LOG_FORMAT(CoreErrorCode.LOG_PARSING001, CoreErrorKind.BAD_REQUEST, "로그 파싱 실패(잘못된 형식)", CoreErrorLevel.WARN),
    FILE_READ_ERROR(CoreErrorCode.FILE_IO001, CoreErrorKind.SERVER_ERROR, "업로드된 파일을 읽을 수 없습니다.", CoreErrorLevel.ERROR)
    ;
    private final CoreErrorCode errorCode;
    private final CoreErrorKind errorKind;
    private final String message;
    private final CoreErrorLevel errorLevel;

    CoreErrorType(CoreErrorCode errorCode, CoreErrorKind errorKind, String message, CoreErrorLevel errorLevel) {
        this.errorCode = errorCode;
        this.errorKind = errorKind;
        this.message = message;
        this.errorLevel = errorLevel;
    }

    public CoreErrorCode getErrorCode() {
        return errorCode;
    }

    public CoreErrorKind getErrorKind() {
        return errorKind;
    }

    public String getMessage() {
        return message;
    }

    public CoreErrorLevel getErrorLevel() {
        return errorLevel;
    }
}
