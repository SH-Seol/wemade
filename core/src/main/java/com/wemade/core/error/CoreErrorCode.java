package com.wemade.core.error;

public enum CoreErrorCode {
    LOG_PARSING001("LP-400"),
    FILE_IO001("IO-500")
    ;
    private final String code;

    CoreErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
