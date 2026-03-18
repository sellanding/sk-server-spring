package kr.sellanding.sk_server_spring.controller;

import java.util.Map;
import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.dto.UserResponse;
import kr.sellanding.sk_server_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable UUID userId) {
        User user = userService.getUser(userId);
        return UserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .bio(user.getBio())
            .role(user.getRole().name())
            .build();
    }

    @PatchMapping("/{userId}/nickname")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateNickname(
        @PathVariable UUID userId,
        @RequestBody Map<String, String> body
    ) {
        userService.updateNickname(userId, body.get("nickname"));
    }

    @PatchMapping("/{userId}/bio")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBio(
        @PathVariable UUID userId,
        @RequestBody Map<String, String> body
    ) {
        userService.updateBio(userId, body.get("bio"));
    }
}
