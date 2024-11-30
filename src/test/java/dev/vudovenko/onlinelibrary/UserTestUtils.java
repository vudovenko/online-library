package dev.vudovenko.onlinelibrary;

import dev.vudovenko.onlinelibrary.security.jwt.JwtTokenManager;
import dev.vudovenko.onlinelibrary.users.UserEntity;
import dev.vudovenko.onlinelibrary.users.UserRepository;
import dev.vudovenko.onlinelibrary.users.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserTestUtils {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenManager jwtTokenManager;

    private static final String DEFAULT_ADMIN_LOGIN = "admin";
    private static final String DEFAULT_USER_LOGIN = "user";

    private static volatile boolean isUsersInitialized = false;

    public String getJwtTokenWithRole(UserRole userRole) {
        synchronized (UserTestUtils.class) {
            if (!isUsersInitialized) {
                initializeTestUsers();
                isUsersInitialized = true;
            }
        }

        return switch (userRole) {
            case ADMIN -> jwtTokenManager.generateToken(DEFAULT_ADMIN_LOGIN);
            case USER -> jwtTokenManager.generateToken(DEFAULT_USER_LOGIN);
        };
    }

    private void initializeTestUsers() {
        createUser(DEFAULT_ADMIN_LOGIN, "admin", UserRole.ADMIN);
        createUser(DEFAULT_USER_LOGIN, "user", UserRole.USER);
    }

    private void createUser(
            String login,
            String password,
            UserRole role
    ) {
        if (userRepository.existsByLogin(login)) {
            return;
        }

        String hashedPass = passwordEncoder.encode(password);
        UserEntity userToSave = new UserEntity(
                null,
                login,
                hashedPass,
                role.name()
        );

        userRepository.save(userToSave);
    }
}
