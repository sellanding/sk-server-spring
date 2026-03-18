package kr.sellanding.sk_server_spring.service;

import jakarta.annotation.PostConstruct;
import kr.sellanding.sk_server_spring.domain.ProfanityTerm;
import kr.sellanding.sk_server_spring.repository.ProfanityTermRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfanityService {

    private final ProfanityTermRepository profanityTermRepository;
    private List<String> cachedTerms = new ArrayList<>();

    @PostConstruct
    public void init() {
        refreshTerms();
    }

    /**
     * DB에서 비속어 목록을 다시 불러와 캐시를 갱신합니다.
     * 새로운 비속어가 추가되었을 때 수동으로 호출할 수 있습니다.
     */
    public void refreshTerms() {
        this.cachedTerms = profanityTermRepository.findAll().stream()
                .map(ProfanityTerm::getTerm)
                .collect(Collectors.toList());
        log.info("Loaded {} profanity terms from database.", cachedTerms.size());
    }

    public List<String> findBlockedTerms(String text) {
        if (text == null || text.isBlank()) return List.of();

        String normalized = normalizeText(text);
        return cachedTerms.stream()
                .filter(normalized::contains)
                .collect(Collectors.toList());
    }

    private String normalizeText(String text) {
        // 소문자 변환 및 한글/영문/숫자를 제외한 특수문자 제거
        return text.toLowerCase().replaceAll("[^a-z0-9가-힣]", "");
    }
}
