package dev.vudovenko.onlinelibrary.author;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

//    @Query(
//            """
//                    SELECT a
//                    FROM AuthorEntity a
//                    JOIN FETCH a.books
//                    """
//    )
//    List<AuthorEntity> findAllWithBooks();

//    @Query("SELECT a FROM AuthorEntity a")
//    @EntityGraph(attributePaths = "books")
//    List<AuthorEntity> findAllWithBooks();

    @Query("SELECT a FROM AuthorEntity a")
    @EntityGraph(value = "author-with-books")
    List<AuthorEntity> findAllWithBooks();
}
