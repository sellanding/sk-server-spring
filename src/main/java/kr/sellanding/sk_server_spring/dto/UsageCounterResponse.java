package kr.sellanding.sk_server_spring.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageCounterResponse {

    private UUID id;
    private Long inkId;
    private LocalDateTime updatedAt;
}
