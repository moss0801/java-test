package com.moss.javatest.book.domain.model;

import com.moss.javatest.book.domain.code.BookType;
import com.querydsl.core.annotations.QueryEmbedded;
import lombok.*;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.OffsetDateTime;

/**
 * 책
 * Id를 직접 생성하는 경우
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
            @AttributeOverride(name="id", column=@Column(name="category_id"))
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

    @Builder
    private Book(BookId id, CategoryId categoryId, BookType bookType, String title, String author, OffsetDateTime published, String isbn13) {
        Assert.notNull(id, "bookId can not be null.");
        this.id = id;
        this.categoryId = categoryId;
        this.bookType = bookType;
        this.title = title;
        this.author = author;
        this.published = published;
        this.isbn13 = isbn13;
    }

    public static class Constraint {
        public static final int NameMaxLength = 100;
        public static final int AuthorMaxLength = 50;
    }
}
