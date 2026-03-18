package kr.sellanding.sk_server_spring.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import kr.sellanding.sk_server_spring.domain.ProfanityTerm;
import kr.sellanding.sk_server_spring.repository.ProfanityTermRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfanityServiceTest {

    private ProfanityService profanityService;

    @Mock
    private ProfanityTermRepository profanityTermRepository;

    @BeforeEach
    void setUp() {
        // 테스트용 비속어 데이터 Mocking
        List<ProfanityTerm> mockTerms = List.of(
            ProfanityTerm.builder().term("씨발").build(),
            ProfanityTerm.builder().term("지랄").build()
        );
        when(profanityTermRepository.findAll()).thenReturn(mockTerms);

        profanityService = new ProfanityService(profanityTermRepository);
        profanityService.init();
    }

    @Test
    @DisplayName("비속어가 포함된 텍스트를 감지한다")
    void findBlockedTerms_detectsProfanity() {
        String text = "이런 씨발 진짜 지랄하네";
        List<String> blocked = profanityService.findBlockedTerms(text);

        assertThat(blocked).contains("씨발", "지랄");
    }

    @Test
    @DisplayName("깨끗한 텍스트는 아무것도 감지하지 않는다")
    void findBlockedTerms_noProfanity() {
        String text = "안녕하세요 반갑습니다.";
        List<String> blocked = profanityService.findBlockedTerms(text);

        assertThat(blocked).isEmpty();
    }

    @Test
    @DisplayName("특수문자가 섞인 비속어도 감지한다")
    void findBlockedTerms_detectsNormalizedProfanity() {
        String text = "씨!발";
        List<String> blocked = profanityService.findBlockedTerms(text);

        assertThat(blocked).contains("씨발");
    }
}
