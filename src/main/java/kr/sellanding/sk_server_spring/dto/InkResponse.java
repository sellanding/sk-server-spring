package kr.sellanding.sk_server_spring.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InkResponse {

    private Long id;
    private String title;
    private String content;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponse user;
    private List<CommentResponse> comments;
}
