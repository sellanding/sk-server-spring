package kr.sellanding.sk_server_spring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record LocalSyncRequest(
    @NotNull
    UUID id,
    
    @NotNull
    @Email
    String email,
    
    @NotNull
    String name
) {}
