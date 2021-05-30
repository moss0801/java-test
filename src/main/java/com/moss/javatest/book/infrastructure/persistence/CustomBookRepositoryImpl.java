package com.moss.javatest.book.infrastructure.persistence;

import com.moss.javatest.book.domain.model.*;
import com.moss.javatest.book.dto.book.BookDto;
import com.moss.javatest.book.dto.book.BooksQuery;
import com.moss.javatest.shared.infrastructure.querydsl.Predicates;
import com.moss.javatest.shared.infrastructure.querydsl.SharedQuerydslRepositorySupport;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CustomBookRepositoryImpl extends SharedQuerydslRepositorySupport implements CustomBookRepository {

    public CustomBookRepositoryImpl() {
        super(Book.class, QBook.class, QBook.book);

        final var book = QBook.book;
        // https://github.com/querydsl/querydsl/issues/1214
        var newExpressions = Arrays.stream(getExpressions()).map(expression -> {
            if (expression.toString().equals("book.categoryId.id")) {
                return as(expression, "categoryId");
            }
            return expression;
        }).toArray(Expression[]::new);
        setExpressions(newExpressions);
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
                    .and().optional(book.categoryId.id::eq, query.getCategoryId())
                .end())
                .orderBy(book.title.asc())
                .fetch();

        return list;
    }
}
