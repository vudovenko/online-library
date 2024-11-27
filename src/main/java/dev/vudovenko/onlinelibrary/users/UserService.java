package dev.vudovenko.onlinelibrary.users;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("Username already taken");
        }
        UserEntity userToSave = new UserEntity(
                null,
                signUpRequest.login(),
                signUpRequest.password(),
                UserRole.USER.name()
        );
        UserEntity saved = userRepository.save(userToSave);

        return new User(
                saved.getId(),
                saved.getLogin(),
                UserRole.valueOf(saved.getRole())
        );
    }
}
