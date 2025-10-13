package com.bizmate.groupware.approval.api;

import com.bizmate.common.exception.VerificationFailedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.bizmate")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    @ExceptionHandler(VerificationFailedException.class)
    public ResponseEntity<?> handleVerification(VerificationFailedException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "code", "BUSINESS_VALIDATION",
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception e) {
        return ResponseEntity.internalServerError().body(Map.of(
                "code", "UNHANDLED",
                "message", "처리 중 오류가 발생했습니다."
        ));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleLock(ObjectOptimisticLockingFailureException e) {
        return ResponseEntity.status(409).body(Map.of(
                "code","CONFLICT",
                "message","동시에 수정되어 다시 시도해주세요."
        ));
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<?> handleValidation(Exception e) {
        // 간단 응답(필요 시 필드별 메시지 수집 로직 추가)
        return ResponseEntity.badRequest().body(Map.of(
                "code", "VALIDATION_ERROR",
                "message", "입력값을 확인해 주세요."
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException e) {
        return ResponseEntity.badRequest().body(Map.of(
                "code", "DATA_INTEGRITY",
                "message", "데이터 무결성 제약 위반입니다."
        ));
    }
}
