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
                    where (:authorName is null or b.authorName = :authorName)
                    and (:cost is null or b.cost < :cost)
                    """
    )
    List<BookEntity> searchBooks(
            @Param("authorName") String authorName,
            @Param("cost") Integer cost,
            Pageable pageable
    );

    @Query(
            value = """
                    select *
                    from books b
                    where (:authorName is null or b.author_name = :authorName)
                    and (:cost is null or b.cost < :cost)
                    """,
            nativeQuery = true
    )
    List<BookEntity> searchBooksNative(
            @Param("authorName") String authorName,
            @Param("cost") Integer cost
    );

    @Transactional
    @Modifying
    @Query(
            """
                    UPDATE BookEntity b
                    SET
                        b.name = :name,
                        b.authorName = :authorName,
                        b.publicationYear = :pubYear,
                        b.pageNumber = :pageNum,
                        b.cost = :cost
                    WHERE b.id = :id
                    """
    )
    void updateBook(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("authorName") String authorName,
            @Param("pubYear") Integer publicationYear,
            @Param("pageNum") Integer pageNumber,
            @Param("cost") Integer cost
    );
}
