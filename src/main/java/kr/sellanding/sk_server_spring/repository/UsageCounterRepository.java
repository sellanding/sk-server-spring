package kr.sellanding.sk_server_spring.repository;

import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.UsageCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UsageCounterRepository
    extends JpaRepository<UsageCounter, UUID> {

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE usage_counters SET ink_count = ink_count + 1 WHERE user_id = :userId", nativeQuery = true)
    void incrementInkCount(@Param("userId") UUID userId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE usage_counters SET ink_count = CASE WHEN ink_count > 0 THEN ink_count - 1 ELSE 0 END WHERE user_id = :userId", nativeQuery = true)
    void decrementInkCount(@Param("userId") UUID userId);
}
