package kr.sellanding.sk_server_spring.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profanity_term")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProfanityTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String term;
}
