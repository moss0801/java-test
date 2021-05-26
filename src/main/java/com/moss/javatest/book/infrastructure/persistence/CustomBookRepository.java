package com.moss.javatest.book.infrastructure.persistence;

import com.moss.javatest.book.domain.model.BookId;

public interface CustomBookRepository {
    BookId newIdentity();
}
