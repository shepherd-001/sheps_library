package com.shepherd.shepslibrary.service.book;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.request.FilterBookRequest;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.repository.BookRepository;
import com.shepherd.shepslibrary.mapper.BookMapper;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import static org.mockito.Mockito.*;

class BookServiceImplTest {
    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void authorOnlySearchUsesAuthorAndAllowsMissingOtherFilters() {
        BookRepository repository = mock(BookRepository.class);
        BookServiceImpl service = new BookServiceImpl(repository, mock(BookMapper.class));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        service.filterBook(new FilterBookRequest(null, "Le Guin", null),
                PaginationRequest.of(1, 10, null, null));
        ArgumentCaptor<Specification<Book>> specification = ArgumentCaptor.forClass(Specification.class);
        verify(repository).findAll(specification.capture(), any(Pageable.class));
        Root<Book> root = mock(Root.class);
        CriteriaBuilder builder = mock(CriteriaBuilder.class);
        Path<String> authorPath = mock(Path.class);
        when(root.<String>get("author")).thenReturn(authorPath);
        when(builder.lower(authorPath)).thenReturn(authorPath);
        specification.getValue().toPredicate(root, mock(CriteriaQuery.class), builder);
        verify(builder).like(authorPath, "%le guin%");
        verify(root, never()).get("title");
        verify(root, never()).get("genre");
    }
}
