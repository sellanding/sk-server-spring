package kr.sellanding.sk_server_spring.repository;

import kr.sellanding.sk_server_spring.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByInkIdOrderByCreatedAtAsc(Long inkId, Pageable pageable);
}
