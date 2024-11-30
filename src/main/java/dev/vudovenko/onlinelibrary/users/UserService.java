package dev.vudovenko.onlinelibrary.users;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("Username already taken");
        }
        String hashedPassword = passwordEncoder.encode(signUpRequest.password());
        UserEntity userToSave = new UserEntity(
                null,
                signUpRequest.login(),
                hashedPassword,
                UserRole.USER.name()
        );
        UserEntity saved = userRepository.save(userToSave);

        return new User(
                saved.getId(),
                saved.getLogin(),
                UserRole.valueOf(saved.getRole())
        );
    }

    public User findByLogin(String loginFromToken) {
        UserEntity userEntity = userRepository.findByLogin(loginFromToken)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return mapToDomain(userEntity);
    }

    private static User mapToDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                UserRole.valueOf(userEntity.getRole())
        );
    }
}
