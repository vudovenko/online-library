package dev.vudovenko.onlinelibrary.book;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BookRepository extends JpaRepository<BookEntity, Long> {

//    List<BookEntity> findAllByAuthorNameIsAndCostLessThan(String authorName, Integer maxCost);

    @Query(
            """
                    select b
                    from BookEntity b
                    where (:authorId is null or b.authorId = :authorId)
                    and (:maxCost is null or b.cost < :maxCost)
                    """
    )
    List<BookEntity> searchBooks(
            @Param("authorId") Long authorId,
            @Param("maxCost") Integer maxCost,
            Pageable pageable
    );

    @Query(
            value = """
                    SELECT * from books b
                    WHERE (:authorId IS NULL OR b.author_id = :authorId)
                    AND (:cost IS NULL OR b.cost < :cost)
                    """,
            nativeQuery = true
    )
    List<BookEntity> searchBooksNative(
            Long authorId,
            @Param("cost") Integer maxCost
    );

    @Transactional
    @Modifying
    @Query(
            """
                    UPDATE BookEntity b
                    SET
                        b.name = :name,
                        b.authorId = :authorId,
                        b.publicationYear = :pubYear,
                        b.pageNumber = :pageNum,
                        b.cost = :cost
                    WHERE b.id = :id
                    """
    )
    void updateBook(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("authorId") Long authorId,
            @Param("pubYear") Integer publicationYear,
            @Param("pageNum") Integer pageNumber,
            @Param("cost") Integer cost
    );
}
