package com.moss.javatest.book.userinterface;

import com.moss.javatest.book.dto.AddBookCommand;
import com.moss.javatest.book.dto.BookDto;
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
    public void add(@RequestBody AddBookCommand command) {
        service.add(command);
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
    public List<BookDto> list() {
        return service.list();
    }
}
