package com.moss.javatest.book.domain.code;

import lombok.Getter;

/**
 * 책 유형
 */
public enum BookType {
    Paper(1),
    Ebook(2),
    ;

    BookType(int code) {
        this.code = code;
    }

    @Getter
    int code;
}
