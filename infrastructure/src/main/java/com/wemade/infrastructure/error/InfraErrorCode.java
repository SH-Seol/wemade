package com.wemade.infrastructure.error;

public enum InfraErrorCode {
    IP_INFO001("IF-101"),
    IP_INFO002("IF-102"),
    IP_INFO003("IF-103"),
    STORAGE001("IF-201")
    ;

    private final String code;

    InfraErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
