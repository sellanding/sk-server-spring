package kr.sellanding.sk_server_spring.exception;

import kr.sellanding.sk_server_spring.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bean Validation (@Valid) 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        
        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> ErrorResponse.FieldError.builder()
                        .field(error.getField())
                        .value(error.getRejectedValue() == null ? "" : error.getRejectedValue().toString())
                        .reason(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.builder()
                .message("Invalid Input Value")
                .code("COMMON_INVALID_INPUT")
                .errors(fieldErrors)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 비즈니스 로직 상의 잘못된 인자 값 전달 시 (IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException", e);
        
        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .code("BUSINESS_INVALID_VALUE")
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 권한 부족 또는 잘못된 상태에서 작업 시 (IllegalStateException)
     */
    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        log.error("handleIllegalStateException", e);
        
        ErrorResponse response = ErrorResponse.builder()
                .message(e.getMessage())
                .code("BUSINESS_ILLEGAL_STATE")
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * 그 외 예상치 못한 모든 서버 내부 에러 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        
        ErrorResponse response = ErrorResponse.builder()
                .message("Internal Server Error")
                .code("SERVER_ERROR")
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
