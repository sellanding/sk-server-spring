package kr.sellanding.sk_server_spring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InkRequest(
    @NotBlank @Size(max = 100) String title,
    @NotBlank @Size(max = 700) String content
) {}
