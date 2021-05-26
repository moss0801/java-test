package com.moss.javatest.book.domain.model;

import com.moss.javatest.book.domain.code.BookType;
import lombok.*;

import javax.persistence.*;
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

    // 분류 Id
    @Setter
    //----
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="id", column=@Column(name = "CATEGORY_ID"))
    })
    private CategoryId categoryId;

    // 책유형
    @Setter
    private BookType bookType;

    // 제목
    @Setter
    private String title;

    // 작가
    @Setter
    private String author;

    // 출간일
    @Setter
    private OffsetDateTime published;

    // ISBN13
    @Setter
    private String isbn13;

    public static Book of(BookId id, BookType bookType, String title, String author, OffsetDateTime published, String isbn13) {
        return Book.builder()
                .id(id)
                .bookType(bookType)
                .title(title)
                .author(author)
                .published(published)
                .isbn13(isbn13)
                .build();
    }


    public static class Constraint {
        public static final int NameMaxLength = 100;
        public static final int AuthorMaxLength = 50;
    }
}
