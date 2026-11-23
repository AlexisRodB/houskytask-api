package com.houskytask.houskytask_api.infrastructure.adapter.out.persistence;

import com.houskytask.houskytask_api.domain.model.User;
import com.houskytask.houskytask_api.domain.port.out.UserRepositoryPort;
import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.entity.UserJpaEntity;
import com.houskytask.houskytask_api.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository userRepository;

    @Override
    public User save(User user) {
        Map<String, String> socials = user.getSocials() != null ? user.getSocials() : Map.of();

        UserJpaEntity entity = UserJpaEntity.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole() != null ? user.getRole() : "USER")
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .contactEmail(user.getContactEmail())
                .githubUrl(socials.getOrDefault("github", ""))
                .linkedinUrl(socials.getOrDefault("linkedin", ""))
                .instagramUrl(socials.getOrDefault("instagram", ""))
                .twitterUrl(socials.getOrDefault("twitter", ""))
                .build();

        return toDomain(userRepository.save(entity));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User toDomain(UserJpaEntity e) {
        Map<String, String> socials = new HashMap<>();
        socials.put("github", e.getGithubUrl() != null ? e.getGithubUrl() : "");
        socials.put("linkedin", e.getLinkedinUrl() != null ? e.getLinkedinUrl() : "");
        socials.put("instagram", e.getInstagramUrl() != null ? e.getInstagramUrl() : "");
        socials.put("twitter", e.getTwitterUrl() != null ? e.getTwitterUrl() : "");

        return User.builder()
                .id(e.getId())
                .fullName(e.getFullName())
                .email(e.getEmail())
                .password(e.getPassword())
                .role(e.getRole() != null ? e.getRole() : "USER")
                .bio(e.getBio() != null ? e.getBio() : "")
                .avatarUrl(e.getAvatarUrl() != null ? e.getAvatarUrl() : "")
                .phone(e.getPhone() != null ? e.getPhone() : "")
                .contactEmail(e.getContactEmail() != null ? e.getContactEmail() : "")
                .socials(socials)
                .build();
    }
}
