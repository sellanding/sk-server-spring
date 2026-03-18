package kr.sellanding.sk_server_spring.repository;

import kr.sellanding.sk_server_spring.domain.ProfanityTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfanityTermRepository extends JpaRepository<ProfanityTerm, Long> {
}
