package com.moss.javatest.book.dto.book;

import com.moss.javatest.book.domain.code.BookType;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 책 추가 Command
 */
@Data
public class AddBookCommand {
    // 책유형
    private BookType bookType;

    // 분류Id
    private Integer categoryId;

    // 제목
    private String title;

    // 작가
    private String author;

    // 출간일
    private OffsetDateTime published;

    // ISBN13
    private String isbn13;
}
