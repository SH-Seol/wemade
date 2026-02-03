package com.wemade.core.error;

public enum CoreErrorCode {
    ;
    private final String code;

    CoreErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
