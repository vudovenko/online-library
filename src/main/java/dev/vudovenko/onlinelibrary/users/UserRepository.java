package dev.vudovenko.onlinelibrary.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);
}
