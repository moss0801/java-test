package com.moss.javatest.book.infrastructure.persistence;

import com.moss.javatest.book.domain.model.BookId;

import java.util.UUID;

public class CustomBookRepositoryImpl implements CustomBookRepository {

    /**
     * 새 Id 생성
     */
    public BookId newIdentity() {
        return BookId.of(UUID.randomUUID().toString());
    }
}
