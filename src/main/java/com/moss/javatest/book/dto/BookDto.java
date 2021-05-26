package com.moss.javatest.book.dto;

import com.moss.javatest.book.domain.code.BookType;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Book Dto
 */
@Data
public class BookDto {
    // Id
    private Integer id;

    // 카테고리Id
    private Integer categoryId;

    // 책유형
    private BookType bookType;

    // 이름
    private String name;

    // 작가
    private String author;

    // 출간일
    private OffsetDateTime published;

    // ISBN13
    private String isbn13;
}
