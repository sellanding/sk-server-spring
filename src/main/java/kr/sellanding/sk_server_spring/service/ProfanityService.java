package kr.sellanding.sk_server_spring.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfanityService {

    private List<String> profanityTerms = new ArrayList<>();
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(
                "profanity_terms.json"
            );
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    profanityTerms = objectMapper.readValue(
                        inputStream,
                        new TypeReference<List<String>>() {}
                    );
                    log.info(
                        "Loaded {} profanity terms.",
                        profanityTerms.size()
                    );
                }
            } else {
                log.warn(
                    "profanity_terms.json not found in resources. Filtering will be disabled."
                );
            }
        } catch (IOException e) {
            log.error("Failed to load profanity terms: {}", e.getMessage());
        }
    }

    public List<String> findBlockedTerms(String text) {
        if (text == null || text.isBlank()) return List.of();

        String normalized = normalizeText(text);
        return profanityTerms
            .stream()
            .filter(normalized::contains)
            .collect(Collectors.toList());
    }

    private String normalizeText(String text) {
        return text.toLowerCase().replaceAll("[^a-zA-Z0-9가-힣]", "");
    }
}
