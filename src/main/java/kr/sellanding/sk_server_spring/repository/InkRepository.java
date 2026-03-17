package kr.sellanding.sk_server_spring.repository;

import java.util.Optional;
import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.Ink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InkRepository extends JpaRepository<Ink, Long> {
    @Query(
        "SELECT i FROM Ink i JOIN FETCH i.user" +
            "WHERE (:keyword IS NULL OR i.title LIKE %keyword% OR i.content LIKE %keyword%)" +
            "AND (:userId IS NULL OR i.user.id = :userId)" +
            "AND (:userName IS NULL OR i.user.name LIKE %userName%)"
    )
    Page<Ink> searchInks(
        @Param("keyword") String keyword,
        @Param("userId") UUID userId,
        @Param("userName") String userName,
        Pageable pageable
    );

    @Query(
        "SELECT i FROM Ink i LEFT JOIN FETCH i.comments c LEFT JOIN FETCH c.user WHERE i.id = :id"
    )
    Optional<Ink> findByIdWithComments(@Param("id") Long id);
}
