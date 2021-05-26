package com.moss.javatest.book.domain.repository;

import com.moss.javatest.book.domain.model.Book;
import com.moss.javatest.book.domain.model.BookId;
import com.moss.javatest.book.infrastructure.persistence.CustomBookRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 책 Repository
 */
@Repository
public interface BookRepository extends JpaRepository<Book, BookId>, CustomBookRepository {
}
