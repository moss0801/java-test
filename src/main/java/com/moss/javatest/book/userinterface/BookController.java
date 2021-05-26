package com.moss.javatest.book.userinterface;

import com.moss.javatest.book.dto.book.*;
import com.moss.javatest.book.service.BookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 책 Controller
 */
@RequestMapping("v1.0/books")
@RestController
public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    /**
     * 추가
     * @param command 추가 Command
     */
    @PostMapping
    public AddBookResult add(@RequestBody AddBookCommand command) {
        return service.add(command);
    }

    /**
     * 조회
     * @param id 책 Id
     * @return 책
     */
    @GetMapping("{id}")
    public BookDto get(@PathVariable String id) {
        return service.get(id);
    }

    /**
     * 목록 조회
     * @return 책 목록
     */
    @GetMapping
    public List<BookDto> list(@ModelAttribute BooksQuery query) {
        return service.list(query);
    }

    /**
     * 수정
     * @param id 책 Id
     * @param command 수정 Command
     */
    @PutMapping("{id}")
    public void update(@PathVariable String id, @RequestBody UpdateBookCommand command) {
        command.setId(id);
        service.update(command);
    }

    /**
     * 삭제
     * @param id 책 Id
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
