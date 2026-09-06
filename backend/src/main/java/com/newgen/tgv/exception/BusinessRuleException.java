package com.newgen.tgv.exception;

import java.util.Map;

public class BusinessRuleException extends RuntimeException {

    private final String errorCode;
    private final Map<String, Object> params;

    public BusinessRuleException(String errorCode, String defaultMessage) {
        this(errorCode, defaultMessage, Map.of());
    }

    public BusinessRuleException(String errorCode, String defaultMessage, Map<String, Object> params) {
        super(defaultMessage);
        this.errorCode = errorCode;
        this.params = params;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
