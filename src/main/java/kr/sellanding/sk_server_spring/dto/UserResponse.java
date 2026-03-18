package kr.sellanding.sk_server_spring.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record UserResponse(
    UUID id,
    String name,
    String email,
    String bio,
    String role
) {}
