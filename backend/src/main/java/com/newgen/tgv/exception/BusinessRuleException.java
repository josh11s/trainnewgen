package com.newgen.tgv.exception;

import com.newgen.tgv.dto.error.BusinessErrorCode;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class BusinessRuleException extends RuntimeException {

    private final BusinessErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final Map<String, Object> params;

    public BusinessRuleException(BusinessErrorCode errorCode, String defaultMessage) {
        this(errorCode, defaultMessage, HttpStatus.UNPROCESSABLE_ENTITY, Map.of());
    }

    public BusinessRuleException(BusinessErrorCode errorCode, String defaultMessage, Map<String, Object> params) {
        this(errorCode, defaultMessage, HttpStatus.UNPROCESSABLE_ENTITY, params);
    }

    public BusinessRuleException(BusinessErrorCode errorCode, String defaultMessage, HttpStatus httpStatus, Map<String, Object> params) {
        super(defaultMessage);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus != null ? httpStatus : HttpStatus.UNPROCESSABLE_ENTITY;
        this.params = params != null ? params : Map.of();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
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
