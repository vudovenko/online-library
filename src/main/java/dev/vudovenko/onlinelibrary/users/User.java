package dev.vudovenko.onlinelibrary.users;

public record User(
        Long id,
        String login,
        UserRole role
) {
}
