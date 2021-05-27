package com.moss.javatest.book.dto.book;

import com.moss.javatest.book.domain.code.BookType;
import lombok.Data;

import java.util.List;

/**
 * 책 목록 쿼리
 */
@Data
public class BooksQuery {
    // 유형 목록
    private List<BookType> bookTypes;

    // 분류 Id
    private Integer categoryId;
}
