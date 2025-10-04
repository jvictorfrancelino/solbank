package br.com.solbank.domain.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusiness(BusinessException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload("conflict", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload("now_found", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleBeanValidation(MethodArgumentNotValidException ex) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(f -> f.getField(), f -> f.getDefaultMessage(), (a,b) -> a));
        return ResponseEntity.badRequest().body(Map.of(
                "error", "validation",
                "when", OffsetDateTime.now().toString(),
                "details", errors
        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleFK(DataIntegrityViolationException ex) {
        return ResponseEntity.
                status(HttpStatus.CONFLICT)
                .body(payload("constraint_violation",
                        "Operação não permitida: Existem registros relacionados"));
    }

    private Map<String, Object> payload(String code, String msg){
        return Map.of("erro", code, "message", msg, "when", OffsetDateTime.now().toString());
    }
}
