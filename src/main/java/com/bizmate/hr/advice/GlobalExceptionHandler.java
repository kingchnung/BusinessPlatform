package com.bizmate.hr.advice;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * [GlobalExceptionHandler]
 * - Controller 계층에서 발생하는 주요 예외를 처리하고
 *   표준화된 JSON 응답(ResponseEntity)으로 반환합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * IllegalStateException 처리 (HTTP 400 Bad Request or 409 Conflict)
     * - 중복 데이터 등록, 비즈니스 로직 위반 등에 사용
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        log.warn("비즈니스 로직 예외 발생: {}", e.getMessage());
        Map<String, String> error = Map.of(
                "error", "ILLEGAL_STATE",
                "message", e.getMessage()
        );
        // 비즈니스 로직 위반은 보통 409 Conflict로 보는 게 합리적입니다.
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * @Valid, @Validated 유효성 검증 실패 처리 (HTTP 400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("유효성 검증 실패: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * EntityNotFoundException 처리 (HTTP 404)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        log.warn("EntityNotFoundException 발생: {}", ex.getMessage());
        Map<String, String> error = Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * JWT 예외 처리 (HTTP 403)
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
        log.error("JWTException 발생: {}", ex.getMessage());
        Map<String, String> error = Map.of(
                "error", "JWT_ERROR",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    /**
     * 그 외 모든 예외 (HTTP 500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllUncaught(Exception ex) {
        log.error("예상치 못한 예외 발생: {}", ex.getMessage(), ex);
        Map<String, String> error = Map.of(
                "error", "INTERNAL_SERVER_ERROR",
                "message", "서버 내부에서 알 수 없는 오류가 발생했습니다."
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
