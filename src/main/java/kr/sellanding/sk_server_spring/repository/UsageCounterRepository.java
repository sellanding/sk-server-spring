package kr.sellanding.sk_server_spring.repository;

import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.UsageCounter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageCounterRepository
    extends JpaRepository<UsageCounter, UUID> {}
