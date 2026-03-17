package kr.sellanding.sk_server_spring.service;

import kr.sellanding.sk_server_spring.domain.Role;
import kr.sellanding.sk_server_spring.domain.UsageCounter;
import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.repository.UserRepository;
import kr.sellanding.sk_server_spring.repository.UsageCounterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UsageCounterRepository usageCounterRepository;

    @Transactional
    public User syncUser(UUID userId, String email, String name) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setName(name);
                    return user;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .id(userId)
                            .email(email)
                            .name(name)
                            .role(Role.USER)
                            .build();
                    User savedUser = userRepository.save(newUser);

                    UsageCounter counter = UsageCounter.builder()
                            .userId(savedUser.getId())
                            .user(savedUser)
                            .inkCount(0L)
                            .build();
                    usageCounterRepository.save(counter);

                    return savedUser;
                });
    }
}
