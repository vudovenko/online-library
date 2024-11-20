package dev.vudovenko.onlinelibrary.author;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorEntityConverter authorEntityConverter;

    public Author createAuthor(Author author) {
        if (authorRepository.existsByName(author.name())) {
            throw new IllegalArgumentException("Author name %s already taken"
                    .formatted(author.name()));
        }
        AuthorEntity entityToSave = authorEntityConverter.toEntity(author);

        return authorEntityConverter.toDomain(
                authorRepository.save(entityToSave)
        );
    }

    public boolean isAuthorExistsById(Long id) {
        return authorRepository.existsById(id);
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(authorEntityConverter::toDomain)
                .toList();
    }
}
