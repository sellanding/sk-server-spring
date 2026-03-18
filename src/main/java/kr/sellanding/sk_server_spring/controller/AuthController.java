package kr.sellanding.sk_server_spring.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.dto.LocalSyncRequest;
import kr.sellanding.sk_server_spring.dto.UserResponse;
import kr.sellanding.sk_server_spring.security.JwtProvider;
import kr.sellanding.sk_server_spring.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncUser(
        @Valid @RequestBody LocalSyncRequest request
    ) {
        // Record의 컴포넌트 접근 메서드: id(), email(), name()
        UUID userId = request.id();
        String userEmail = request.email();
        String userName = request.name();

        User user = authService.syncUser(userId, userEmail, userName);

        // 로컬 JWT 발급
        String token = jwtProvider.createToken(user.getId(), user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put(
            "user",
            UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build()
        );
        response.put("access_token", token);

        return ResponseEntity.ok(response);
    }
}
