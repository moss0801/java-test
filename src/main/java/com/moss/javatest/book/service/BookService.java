package com.moss.javatest.book.service;


import com.moss.javatest.book.domain.model.Book;
import com.moss.javatest.book.domain.model.BookId;
import com.moss.javatest.book.domain.model.CategoryId;
import com.moss.javatest.book.domain.repository.BookRepository;
import com.moss.javatest.book.dto.book.*;
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
    private final CategoryService categoryService;

    public BookService(BookRepository repository, CategoryService categoryService) {
        this.repository = repository;
        this.categoryService = categoryService;
    }

    /**
     * 책 추가
     * @param command 책 추가 Command
     */
    @Transactional
    public AddBookResult add(AddBookCommand command) {
        // 분류 존재 확인
        if (!categoryService.exist(command.getCategoryId())) {
            throw new RuntimeException("category is not exist.");
        }
        
        // 생성
        BookId id = repository.newIdentity();
        var book = Book.builder().id(id).build();
        DtoAssembler.map(command, book, (dto, model) -> {
            model.setCategoryId(dto.getCategoryId());
            return model;
        });

        // 저장
        repository.save(book);

        // 결과
        AddBookResult result = new AddBookResult();
        result.setId(book.getId().getId());
        return result;
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
    public List<BookDto> list(BooksQuery query) {
        return DtoAssembler.to(repository.findAll(query), BookDto.class);
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

        // 분류 존재 확인
        if (!categoryService.exist(command.getCategoryId())) {
            throw new RuntimeException("category is not exist.");
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
