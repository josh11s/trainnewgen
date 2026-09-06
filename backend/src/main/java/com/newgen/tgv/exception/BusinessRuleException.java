package com.newgen.tgv.exception;

import com.newgen.tgv.dto.error.BusinessErrorCode;

import java.util.Map;

public class BusinessRuleException extends RuntimeException {

    private final BusinessErrorCode errorCode;
    private final Map<String, Object> params;

    public BusinessRuleException(BusinessErrorCode errorCode, String defaultMessage) {
        this(errorCode, defaultMessage, Map.of());
    }

    public BusinessRuleException(BusinessErrorCode errorCode, String defaultMessage, Map<String, Object> params) {
        super(defaultMessage);
        this.errorCode = errorCode;
        this.params = params;
    }

    public BusinessErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorCodeValue() {
        return errorCode != null ? errorCode.getCode() : null;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
