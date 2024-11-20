package dev.vudovenko.onlinelibrary.author;

import dev.vudovenko.onlinelibrary.book.BookEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authors")
public class AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    private Integer birthYear;

    @OneToMany
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Set<BookEntity> books;
}
