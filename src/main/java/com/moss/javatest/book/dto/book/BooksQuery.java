package com.moss.javatest.book.dto.book;

import lombok.Data;

/**
 * 책 목록 쿼리
 */
@Data
public class BooksQuery {
    // 분류 Id
    private Integer categoryId;
}
