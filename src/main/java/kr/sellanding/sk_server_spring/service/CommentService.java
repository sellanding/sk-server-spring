package kr.sellanding.sk_server_spring.service;

import java.util.List;
import kr.sellanding.sk_server_spring.domain.Comment;
import kr.sellanding.sk_server_spring.domain.Ink;
import kr.sellanding.sk_server_spring.domain.Role;
import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.dto.CommentRequest;
import kr.sellanding.sk_server_spring.repository.CommentRepository;
import kr.sellanding.sk_server_spring.repository.InkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final InkRepository inkRepository;
    private final ProfanityService profanityService;

    @Transactional
    public Comment createComment(CommentRequest request, User user) {
        List<String> blocked = profanityService.findBlockedTerms(
            request.getContent()
        );
        if (!blocked.isEmpty()) {
            throw new IllegalArgumentException(
                "Blocked terms found: " + blocked
            );
        }

        Ink ink = inkRepository
            .findById(request.getInkId())
            .orElseThrow(() -> new IllegalArgumentException("Ink not found"));

        Comment comment = Comment.builder()
            .content(request.getContent())
            .user(user)
            .ink(ink)
            .build();

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository
            .findById(id)
            .orElseThrow(() ->
                new IllegalArgumentException("Comment not found")
            );

        if (
            !comment.getUser().getId().equals(user.getId()) &&
            user.getRole() != Role.ADMIN
        ) {
            throw new IllegalStateException("Access denied");
        }

        commentRepository.delete(comment);
    }
}
