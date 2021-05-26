package com.moss.javatest.book.dto;

import com.moss.javatest.book.domain.code.BookType;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 책 수정 Command
 */
@Data
public class UpdateBookCommand {
    private String id;

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
