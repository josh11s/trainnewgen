package com.newgen.tgv.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String PROBLEM_BASE_URL = "https://api.newgentgv.com/errors/";

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                ex.getStatusCode(),
                ex.getReason() != null ? ex.getReason() : ex.getMessage()
        );
        String typeSlug = ex.getStatusCode().equals(HttpStatus.CONFLICT) ? "conflict" : "not-found";
        String title = ex.getStatusCode().equals(HttpStatus.CONFLICT) ? "Conflict" : "Resource Not Found";
        problem.setType(URI.create(PROBLEM_BASE_URL + typeSlug));
        problem.setTitle(title);
        return problem;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ProblemDetail handleBusinessRuleException(BusinessRuleException ex) {
        HttpStatus status = ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.UNPROCESSABLE_ENTITY;
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                ex.getMessage()
        );
        problem.setType(URI.create(PROBLEM_BASE_URL + ex.getErrorCodeValue()));
        problem.setTitle(status == HttpStatus.CONFLICT ? "Seat Conflict" : "Business Rule Violation");
        problem.setProperty("errorCode", ex.getErrorCodeValue());
        if (!ex.getParams().isEmpty()) {
            problem.setProperty("params", ex.getParams());
        }
        if (ex instanceof SeatAlreadyReservedException seatEx && !seatEx.getUnavailableSeats().isEmpty()) {
            problem.setProperty("unavailableSeats", seatEx.getUnavailableSeats());
        }
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed"
        );
        problem.setType(URI.create(PROBLEM_BASE_URL + "validation-error"));
        problem.setTitle("Validation Error");

        List<Map<String, Object>> invalidParams = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String field = propertyPath.contains(".") ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1) : propertyPath;
            String constraintName = violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName();

            Map<String, Object> param = new HashMap<>();
            param.put("name", field);
            param.put("code", constraintName);
            param.put("messageKey", "validation." + field + "." + constraintName.toLowerCase());
            param.put("message", violation.getMessage());
            invalidParams.add(param);
        }

        problem.setProperty("invalidParams", invalidParams);
        return problem;
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Required parameter is missing: " + ex.getParameterName()
        );
        problem.setType(URI.create(PROBLEM_BASE_URL + "missing-parameter"));
        problem.setTitle("Missing Parameter");

        Map<String, Object> param = new HashMap<>();
        param.put("name", ex.getParameterName());
        param.put("code", "Required");
        param.put("messageKey", "validation." + ex.getParameterName() + ".required");
        param.put("message", "Parameter '" + ex.getParameterName() + "' is required");

        problem.setProperty("invalidParams", List.of(param));
        return ResponseEntity.status(status).headers(headers).body(problem);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid parameter format: " + ex.getName()
        );
        problem.setType(URI.create(PROBLEM_BASE_URL + "type-mismatch"));
        problem.setTitle("Type Mismatch");

        Map<String, Object> param = new HashMap<>();
        param.put("name", ex.getName());
        param.put("code", "TypeMismatch");
        param.put("messageKey", "validation." + ex.getName() + ".type");
        param.put("message", "Invalid value for parameter '" + ex.getName() + "'");

        problem.setProperty("invalidParams", List.of(param));
        return problem;
    }
}
