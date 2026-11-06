package com.shepherd.shepslibrary.specification;

import com.shepherd.shepslibrary.data.model.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
            (title == null || title.isBlank())
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.trim().toLowerCase(java.util.Locale.ROOT) + "%");
    }

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, criteriaBuilder) ->
            (author == null || author.isBlank())
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.trim().toLowerCase(java.util.Locale.ROOT) + "%");
    }

    public static Specification<Book> hasGenre(String genre) {
        return (root, query, criteriaBuilder) ->
            (genre == null || genre.isBlank())
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")), "%" + genre.trim().toLowerCase(java.util.Locale.ROOT) + "%");
    }
}