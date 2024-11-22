package dev.vudovenko.onlinelibrary.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    Boolean existsByName(String name);

    @Transactional
    @Modifying
    @Query(
            """
                    UPDATE BookEntity b
                    SET b.authorId = NULL
                    WHERE b.authorId = :authorId
                    """
    )
    void deleteAuthorFromBooks(Long authorId);
}
