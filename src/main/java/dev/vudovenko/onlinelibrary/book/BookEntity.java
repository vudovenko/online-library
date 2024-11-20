package dev.vudovenko.onlinelibrary.book;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "author_id")
    private Long authorId;

    @Column(name = "pub_year", nullable = false)
    Integer publicationYear;

    @Column(name = "page_num", nullable = false)
    Integer pageNumber;

    @Column(name = "cost", nullable = false)
    Integer cost;
}
