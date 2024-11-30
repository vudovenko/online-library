package dev.vudovenko.onlinelibrary.book.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<BookPurchaseEntity, Long> {

    boolean existsByUserIdAndBookId(Long userId, Long bookId);
}
