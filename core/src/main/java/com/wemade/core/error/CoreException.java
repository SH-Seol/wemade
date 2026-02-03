package com.wemade.core.error;

public class CoreException extends RuntimeException {
    private final CoreErrorType errorType;
    private final Object data;

    public CoreException(CoreErrorType errorType) {
        this.errorType = errorType;
        this.data = null;
    }

    public CoreException(CoreErrorType errorType, Object data) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.data = data;
    }

    public CoreException(CoreErrorType errorType, String customMessage) {
        super(customMessage);
        this.errorType = errorType;
        this.data = null;
    }

    public CoreErrorType getErrorType() {
        return errorType;
    }

    public Object getData() {
        return data;
    }
}
