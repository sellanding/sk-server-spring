package kr.sellanding.sk_server_spring.repository;

import java.util.Optional;
import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);
}
