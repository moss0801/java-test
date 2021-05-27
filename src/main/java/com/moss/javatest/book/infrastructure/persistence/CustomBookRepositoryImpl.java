package com.moss.javatest.book.infrastructure.persistence;

import com.moss.javatest.book.domain.model.Book;
import com.moss.javatest.book.domain.model.BookId;
import com.moss.javatest.book.domain.model.QBook;
import com.moss.javatest.book.dto.book.BookDto;
import com.moss.javatest.book.dto.book.BooksQuery;
import com.moss.javatest.shared.infrastructure.querydsl.Predicates;
import com.moss.javatest.shared.infrastructure.querydsl.SharedQuerydslRepositorySupport;

import java.util.List;
import java.util.UUID;

public class CustomBookRepositoryImpl extends SharedQuerydslRepositorySupport implements CustomBookRepository {

    public CustomBookRepositoryImpl() {
        super(Book.class, QBook.class, QBook.book);
    }

    /**
     * 새 Id 생성
     */
    public BookId newIdentity() {
        return BookId.of(UUID.randomUUID().toString());
    }

    /**
     * 책 목록 조회
     * @param query 쿼리
     * @return
     */
    @Override
    public List<BookDto> findAll(BooksQuery query) {
        final var book = QBook.book;
        var list = select(BookDto.class, getExpressions())
                .from(book)
                .where(Predicates.start()
                    .optional(book.bookType::in, query.getBookTypes())
                    .and().optional(book.categoryId::eq, query.getCategoryId())
                .end())
                .orderBy(book.title.asc())
                .fetch();

        return list;
    }
}
