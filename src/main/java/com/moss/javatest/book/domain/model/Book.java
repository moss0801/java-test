package com.moss.javatest.book.domain.model;

import com.moss.javatest.book.domain.code.BookType;
import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * 책
 * Id를 직접 생성하는 경우
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
//----
@Entity
@Table(name = "Books")
public class Book {
    // Id
    @EmbeddedId
    @EqualsAndHashCode.Include
    private BookId id;

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


    public static class Constraint {
        public static final int NameMaxLength = 100;
        public static final int AuthorMaxLength = 50;
    }
}
