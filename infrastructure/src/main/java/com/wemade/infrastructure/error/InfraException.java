package com.wemade.infrastructure.error;

public class InfraException extends RuntimeException {

    private final InfraErrorType errorType;

    public InfraException(InfraErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public InfraException(InfraErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public InfraException(InfraErrorType errorType, Throwable cause) {
        super(errorType.getMessage(), cause);
        this.errorType = errorType;
    }

    public InfraException(InfraErrorType errorType, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public InfraErrorType getErrorType() {
        return errorType;
    }
}
