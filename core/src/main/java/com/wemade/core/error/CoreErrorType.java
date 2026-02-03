package com.wemade.core.error;

public enum CoreErrorType {
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
