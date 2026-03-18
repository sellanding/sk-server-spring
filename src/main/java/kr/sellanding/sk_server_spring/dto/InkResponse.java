package kr.sellanding.sk_server_spring.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record InkResponse(
    Long id,
    String title,
    String content,
    UUID userId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UserResponse user,
    List<CommentResponse> comments
) {}
