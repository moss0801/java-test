package com.moss.javatest.book.infrastructure.persistence;

import com.moss.javatest.book.domain.model.BookId;
import com.moss.javatest.book.dto.book.BookDto;
import com.moss.javatest.book.dto.book.BooksQuery;

import java.util.List;

public interface CustomBookRepository {
    /**
     * Id 생성
     * @return Id
     */
    BookId newIdentity();

    /**
     * 책 목록 조회
     * @param query 쿼리
     * @return 책Dto 목록
     */
    List<BookDto> findAll(BooksQuery query);
}
