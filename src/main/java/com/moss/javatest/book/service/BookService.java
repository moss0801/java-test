package com.moss.javatest.book.service;


import com.moss.javatest.book.domain.model.Book;
import com.moss.javatest.book.domain.model.BookId;
import com.moss.javatest.book.domain.model.CategoryId;
import com.moss.javatest.book.domain.repository.BookRepository;
import com.moss.javatest.book.dto.AddBookCommand;
import com.moss.javatest.book.dto.BookDto;
import com.moss.javatest.book.dto.UpdateBookCommand;
import com.moss.javatest.shared.dto.DtoAssembler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 책 서비스
 */
@Service
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    /**
     * 책 추가
     * @param command 책 추가 Command
     */
    @Transactional
    public void add(AddBookCommand command) {
        BookId id = repository.newIdentity();
        var book = Book.builder().id(id).build();
        DtoAssembler.map(command, book, (dto, model) -> {
            model.setCategoryId(CategoryId.of(dto.getCategoryId()));
            return model;
        });

        repository.save(book);
    }

    /**
     * 책 조회
     * @param id 책 id
     * @return 책 Dto
     */
    @Transactional(readOnly = true)
    public BookDto get(String id) {
        BookDto result;
        var bookOptional = repository.findById(BookId.of(id));
        if (bookOptional.isEmpty()) {
            return null;
        }
        return DtoAssembler.to(bookOptional.get(), BookDto.class);
    }

    /**
     * 책 목록 조회
     * @return 책 Dto 목록
     */
    @Transactional(readOnly = true)
    public List<BookDto> list() {
        return DtoAssembler.to(repository.findAll(), BookDto.class);
    }

    /**
     * 책 수정
     * @param command 책 수정 Command
     */
    @Transactional
    public void update(UpdateBookCommand command) {
        // 존재 확인
        var bookId = BookId.of(command.getId());
        var bookOptional = repository.findById(bookId);
        if (bookOptional.isEmpty()) {
            throw new RuntimeException("book is not exist.");
        }

        // 수정
        var book = bookOptional.get();
        book.setBookType(command.getBookType());
        book.setTitle(command.getTitle());
        book.setAuthor(command.getAuthor());
        book.setIsbn13(command.getIsbn13());
        book.setPublished(command.getPublished());
    }

    /**
     * 책 삭제
     * @param id 책 id
     */
    @Transactional
    public void delete(String id) {
        BookId bookId = BookId.of(id);
        var bookOptional = repository.findById(BookId.of(id));
        if (bookOptional.isEmpty()) {
            throw new RuntimeException("book is not exist.");
        }

        repository.deleteById(bookId);
    }
}
