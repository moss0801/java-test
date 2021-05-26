package com.moss.javatest.book.dto.book;

import com.moss.javatest.book.domain.code.BookType;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Book Dto
 */
@Data
public class BookDto {
    // Id
    private String id;

    // 카테고리Id
    private Integer categoryId;

    // 책유형
    private BookType bookType;

    // 제목
    private String title;

    // 작가
    private String author;

    // 출간일
    private OffsetDateTime published;

    // ISBN13
    private String isbn13;
}
