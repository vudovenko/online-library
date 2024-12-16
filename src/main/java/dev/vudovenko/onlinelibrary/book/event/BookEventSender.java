package dev.vudovenko.onlinelibrary.book.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Log4j2
@Component
@RequiredArgsConstructor
public class BookEventSender {

    private final KafkaTemplate<Long, BookKafkaEvent> kafkaTemplate;

    public void sendEvent(BookKafkaEvent bookKafkaEvent) {
        log.info("Sending event: event = {}", bookKafkaEvent);
        CompletableFuture<SendResult<Long, BookKafkaEvent>> result = kafkaTemplate.send(
                "books-topic",
                bookKafkaEvent.bookId(),
                bookKafkaEvent
        );

        result.thenAccept(sendResult -> log.info("Send successful: result = {}", sendResult));
    }
}
