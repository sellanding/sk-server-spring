package kr.sellanding.sk_server_spring.controller;

import kr.sellanding.sk_server_spring.repository.InkRepository;
import kr.sellanding.sk_server_spring.repository.UsageCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GuestbookController {

    private final InkRepository inkRepository;
    private final UsageCounterRepository usageCounterRepository;

    @GetMapping("/stats/today")
    public Map<String, Object> getTodayStats() {
        long inkCount = inkRepository.count();
        return Map.of(
            "visitor_count", usageCounterRepository.count(),
            "ink_count", inkCount
        );
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Spring Boot API Server is running";
    }
}
