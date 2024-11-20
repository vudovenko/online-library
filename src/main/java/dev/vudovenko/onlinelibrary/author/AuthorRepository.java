package dev.vudovenko.onlinelibrary.author;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    Boolean existsByName(String name);
}
