package com.shepherd.shepslibrary.specification;

import com.shepherd.shepslibrary.data.model.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> hasTitle(String title) {
        return (root, query, criteriaBuilder) ->
            title.isBlank()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, criteriaBuilder) ->
            author.isBlank()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    public static Specification<Book> hasGenre(String genre) {
        return (root, query, criteriaBuilder) ->
            genre.isBlank()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")), "%" + genre.toLowerCase() + "%");
    }
}