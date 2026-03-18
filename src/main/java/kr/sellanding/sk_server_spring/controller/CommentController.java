package kr.sellanding.sk_server_spring.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.Comment;
import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.dto.CommentRequest;
import kr.sellanding.sk_server_spring.dto.CommentResponse;
import kr.sellanding.sk_server_spring.dto.UserResponse;
import kr.sellanding.sk_server_spring.service.CommentService;
import kr.sellanding.sk_server_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
        @Valid @RequestBody CommentRequest request,
        @AuthenticationPrincipal String userId
    ) {
        User user = userService.getUser(UUID.fromString(userId));
        Comment comment = commentService.createComment(request, user);
        return convertToDto(comment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
        @PathVariable Long id,
        @AuthenticationPrincipal String userId
    ) {
        User user = userService.getUser(UUID.fromString(userId));
        commentService.deleteComment(id, user);
    }

    private CommentResponse convertToDto(Comment comment) {
        return CommentResponse.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .userId(comment.getUser().getId())
            .inkId(comment.getInk().getId())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .user(
                UserResponse.builder()
                    .id(comment.getUser().getId())
                    .name(comment.getUser().getName())
                    .email(comment.getUser().getEmail())
                    .role(comment.getUser().getRole().name())
                    .build()
            )
            .build();
    }
}
