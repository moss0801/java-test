package com.moss.javatest.book.service;


import com.moss.javatest.book.domain.repository.BookRepository;

/**
 * 책 서비스
 */
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    
}
