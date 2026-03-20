package kr.sellanding.sk_server_spring.service;

import java.util.List;
import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.Ink;
import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.dto.InkRequest;
import kr.sellanding.sk_server_spring.repository.InkRepository;
import kr.sellanding.sk_server_spring.repository.UsageCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InkService {

    private final InkRepository inkRepository;
    private final UsageCounterRepository usageCounterRepository;
    private final ProfanityService profanityService;

    public Page<Ink> getInks(
        String keyword,
        UUID userId,
        String userName,
        Pageable pageable
    ) {
        return inkRepository.searchInks(keyword, userId, userName, pageable);
    }

    public Ink getInk(Long id) {
        return inkRepository
            .findByIdWithComments(id)
            .orElseThrow(() -> new IllegalArgumentException("Ink not found"));
    }

    @Transactional
    public Ink createInk(InkRequest request, User user) {
        List<String> blocked = profanityService.findBlockedTerms(
            request.content()
        );
        if (!blocked.isEmpty()) {
            throw new IllegalArgumentException(
                "Blocked terms found: " + blocked
            );
        }

        Ink ink = Ink.builder()
            .title(request.title())
            .content(request.content())
            .user(user)
            .build();

        Ink savedInk = inkRepository.save(ink);

        usageCounterRepository.incrementInkCount(user.getId());

        return savedInk;
    }

    @Transactional
    public void deleteInk(Long id, User user) {
        Ink ink = inkRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Ink not found"));

        if (
            !ink.getUser().getId().equals(user.getId()) &&
            !user.getRole().getKey().equals("ROLE_ADMIN")
        ) {
            throw new IllegalStateException("Access denied");
        }

        inkRepository.delete(ink);

        usageCounterRepository.decrementInkCount(ink.getUser().getId());
    }
}
