package dev.vudovenko.onlinelibrary.book.purchase;

import dev.vudovenko.onlinelibrary.book.Book;
import dev.vudovenko.onlinelibrary.book.BookService;
import dev.vudovenko.onlinelibrary.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;

@Log4j2
@RequiredArgsConstructor
@Service
public class PurchaseService {

    private final BookService bookService;
    private final PurchaseRepository purchaseRepository;

    public void performBookPurchase(
            User user,
            Long bookId
    ) {
        Book book = bookService.findById(bookId);

        if (purchaseRepository.existsByUserIdAndBookId(user.id(), bookId)) {
            throw new IllegalArgumentException("The user has already bought this book");
        }

        BookPurchaseEntity purchase = new BookPurchaseEntity(
                null,
                bookId,
                user.id(),
                Timestamp.from(Instant.now()),
                book.cost()
        );

        purchaseRepository.save(purchase);

        log.info("User has successfully purchased the book: user={}, book={}",
                user.id(), book.id());
    }
}
