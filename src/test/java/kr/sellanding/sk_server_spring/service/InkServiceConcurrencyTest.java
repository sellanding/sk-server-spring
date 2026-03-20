package kr.sellanding.sk_server_spring.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class InkServiceConcurrencyTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("기존 방식(Read-Modify-Write) 대조: 100개의 스레드가 동시에 실행할 때 데이터 유실이 발생함")
    void concurrency_Test_Old_Way_With_Loss() throws InterruptedException {
        // Given: 테스트 데이터 초기화
        UUID userId = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO users (id, name, email, role, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())",
            userId.toString(), "old_tester", "old@example.com", "USER");
        jdbcTemplate.update("INSERT INTO usage_counters (user_id, ink_count, updated_at, created_at) VALUES (?, 0, NOW(), NOW())",
            userId.toString());

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // When: 기존 방식 (읽기 -> 자바에서 계산 -> 쓰기)
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    // 1. 읽기 (Read)
                    Long currentCount = jdbcTemplate.queryForObject(
                        "SELECT ink_count FROM usage_counters WHERE user_id = ?", 
                        Long.class, userId.toString());
                    
                    // 2. 자바에서 연산 (Modify)
                    Long nextCount = currentCount + 1;
                    
                    // 3. 쓰기 (Write)
                    jdbcTemplate.update("UPDATE usage_counters SET ink_count = ? WHERE user_id = ?", 
                        nextCount, userId.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();

        // Then: 결과 확인 (거의 확실하게 100보다 작음)
        Long finalCount = jdbcTemplate.queryForObject(
            "SELECT ink_count FROM usage_counters WHERE user_id = ?", 
            Long.class, userId.toString());
        
        System.out.println("[Old Way Result] Final Count: " + finalCount + " / Expected: 100");
        assertThat(finalCount).isLessThan((long) threadCount); // 유실 증명
    }

    @Test
    @DisplayName("개선 방식(Atomic Update) 대조: 100개의 스레드가 동시에 실행해도 데이터 유실이 발생하지 않음")
    void concurrency_Test_New_Way_No_Loss() throws InterruptedException {
        // Given: 테스트 데이터 초기화
        UUID userId = UUID.randomUUID();
        jdbcTemplate.update("INSERT INTO users (id, name, email, role, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())",
            userId.toString(), "new_tester", "new@example.com", "USER");
        jdbcTemplate.update("INSERT INTO usage_counters (user_id, ink_count, updated_at, created_at) VALUES (?, 0, NOW(), NOW())",
            userId.toString());

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // When: 개선 방식 (DB에서 직접 연산)
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    // Atomic Update (SET ink_count = ink_count + 1)
                    jdbcTemplate.update("UPDATE usage_counters SET ink_count = ink_count + 1 WHERE user_id = ?", 
                        userId.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();

        // Then: 결과 확인 (정확히 100)
        Long finalCount = jdbcTemplate.queryForObject(
            "SELECT ink_count FROM usage_counters WHERE user_id = ?", 
            Long.class, userId.toString());
        
        System.out.println("[New Way Result] Final Count: " + finalCount + " / Expected: 100");
        assertThat(finalCount).isEqualTo((long) threadCount); // 무결성 증명
    }
}
