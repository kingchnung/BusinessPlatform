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
 * - Controller 계층에서 발생하는 주요 예외를 처리하고 표준화된 JSON 응답을 반환합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 또는 @Validated 유효성 검증 실패 시 처리 (HTTP 400 Bad Request)
     * - DTO의 `@NotBlank`, `@NotNull` 등의 검증 실패 시 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("MethodArgumentNotValidException 발생: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        // 여러 필드 오류를 Map에 담아 클라이언트에 전달
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); // 400
    }

    /**
     * EntityNotFoundException 처리 (HTTP 404 Not Found)
     * - Service 계층에서 데이터베이스에 없는 엔티티를 조회할 때 발생
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.warn("EntityNotFoundException 발생: {}", ex.getMessage());

        Map<String, String> error = Map.of(
                "error", "NOT_FOUND",
                "message", ex.getMessage()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404
    }

    // ★ 선택 사항: JWT 관련 예외 및 사용자 정의 예외 처리

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtException(JwtException ex) {
        log.error("JWTException 발생: {}", ex.getMessage());

        Map<String, String> error = Map.of(
            "error", "JWT_ERROR",
            "message", ex.getMessage()
        );
        // 토큰 오류는 보통 403 Forbidden으로 처리될 수 있습니다. (인증은 통과했으나 접근 거부)
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN); // 403
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.warn("RuntimeException 발생: {}", ex.getMessage());

        Map<String, Object> error = new HashMap<>();
        error.put("error", ex.getMessage()); // 🔹 프론트에서 err.response.data.error 로 접근 가능
        error.put("timestamp", System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400
    }


    /**
     * 기타 예상치 못한 모든 예외 처리 (HTTP 500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllUncaughtException(Exception ex) {
        log.error("예상치 못한 예외 발생: {}", ex.getMessage(), ex);

        Map<String, String> error = Map.of(
                "error", "INTERNAL_SERVER_ERROR",
                "message", "서버 내부에서 알 수 없는 오류가 발생했습니다."
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR); // 500
    }


}