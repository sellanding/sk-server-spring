package kr.sellanding.sk_server_spring.service;

import kr.sellanding.sk_server_spring.domain.User;
import kr.sellanding.sk_server_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public void updateNickname(UUID userId, String nickname) {
        User user = getUser(userId);
        user.setName(nickname);
    }

    @Transactional
    public void updateBio(UUID userId, String bio) {
        User user = getUser(userId);
        user.setBio(bio);
    }
}
