package kr.sellanding.sk_server_spring.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.Ink;
import kr.sellanding.sk_server_spring.domain.Role;
import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.repository.InkRepository;
import kr.sellanding.sk_server_spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StopWatch;

@SpringBootTest
@ActiveProfiles("test")
public class InkServicePagingTest {

    @Autowired
    private InkService inkService;

    @Autowired
    private InkRepository inkRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private static final int TOTAL_DATA = 1000; // H2 환경이므로 적절히 1000건으로 테스트

    @BeforeEach
    void setUp() {
        inkRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();

        testUser = User.builder()
            .id(UUID.randomUUID())
            .name("tester")
            .email("tester@example.com")
            .role(Role.USER)
            .build();
        userRepository.save(testUser);

        // 1000건의 대량 데이터 삽입
        List<Ink> inks = new ArrayList<>();
        for (int i = 1; i <= TOTAL_DATA; i++) {
            inks.add(Ink.builder()
                .title("Title " + i)
                .content("Content " + i)
                .user(testUser)
                .build());
        }
        inkRepository.saveAll(inks);
    }

    @Test
    @DisplayName("Offset 기반 페이징: 뒤로 갈수록 모든 데이터를 읽어야 하므로 비효율적임")
    void offset_Paging_Test() {
        // Given: 마지막 페이지 근처 (990번째 데이터부터 10건)
        int pageNumber = 99; // 0-based index, 100번째 페이지
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // When
        Page<Ink> result = inkService.getInks(null, null, null, pageable);

        stopWatch.stop();

        // Then
        assertThat(result.getContent()).hasSize(pageSize);
        System.out.println("[Offset Paging Result] Time: " + stopWatch.getTotalTimeMillis() + "ms");
    }

    @Test
    @DisplayName("No-Offset 기반 페이징: 마지막 ID를 기준으로 인덱스를 타므로 항상 일정한 성능을 유지함")
    void no_Offset_Paging_Test() {
        // Given: 앞서 990개를 이미 읽었다고 가정하고, 991번째 데이터의 ID를 찾음
        // (실제 상황에서는 프론트엔드가 마지막 데이터의 ID를 넘겨줌)
        List<Ink> firstNineHundred = inkRepository.findAll(Sort.by("id").descending());
        Long lastSeenId = firstNineHundred.get(989).getId(); // 990번째 데이터의 ID

        int pageSize = 10;
        Pageable pageable = PageRequest.of(0, pageSize); // Offset은 0으로 고정

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // When
        List<Ink> result = inkService.getInksNoOffset(lastSeenId, null, null, null, pageable);

        stopWatch.stop();

        // Then
        assertThat(result).hasSize(pageSize);
        assertThat(result.get(0).getId()).isLessThan(lastSeenId); // 마지막 ID보다 작은 데이터들만 조회됨
        System.out.println("[No-Offset Paging Result] Time: " + stopWatch.getTotalTimeMillis() + "ms");
    }
}
