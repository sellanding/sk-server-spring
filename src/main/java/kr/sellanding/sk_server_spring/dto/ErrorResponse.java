package kr.sellanding.sk_server_spring.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final String message;
    private final String code;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<FieldError> errors;

    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String value;
        private final String reason;
    }
}
