package kr.sellanding.sk_server_spring.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.Ink;
import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.dto.InkRequest;
import kr.sellanding.sk_server_spring.dto.InkResponse;
import kr.sellanding.sk_server_spring.dto.UserResponse;
import kr.sellanding.sk_server_spring.service.InkService;
import kr.sellanding.sk_server_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inks")
@RequiredArgsConstructor
public class InkController {

    private final InkService inkService;
    private final UserService userService;

    @GetMapping
    public Page<InkResponse> getInks(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) UUID userId,
        @RequestParam(required = false) String userName,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return inkService
            .getInks(keyword, userId, userName, pageable)
            .map(this::convertToDto);
    }

    @GetMapping("/{id}")
    public InkResponse getInk(@PathVariable Long id) {
        return convertToDto(inkService.getInk(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InkResponse createInk(
        @Valid @RequestBody InkRequest request,
        @AuthenticationPrincipal String userId
    ) {
        User user = userService.getUser(UUID.fromString(userId));
        return convertToDto(inkService.createInk(request, user));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInk(
        @PathVariable Long id,
        @AuthenticationPrincipal String userId
    ) {
        User user = userService.getUser(UUID.fromString(userId));
        inkService.deleteInk(id, user);
    }

    private InkResponse convertToDto(Ink ink) {
        return InkResponse.builder()
            .id(ink.getId())
            .title(ink.getTitle())
            .content(ink.getContent())
            .userId(ink.getUser().getId())
            .createdAt(ink.getCreatedAt())
            .updatedAt(ink.getUpdatedAt())
            .user(
                UserResponse.builder()
                    .id(ink.getUser().getId())
                    .name(ink.getUser().getName())
                    .email(ink.getUser().getEmail())
                    .role(ink.getUser().getRole().name())
                    .build()
            )
            .build();
    }
}
