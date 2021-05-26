package com.moss.javatest.book.userinterface;

import com.moss.javatest.book.dto.category.AddCategoryCommand;
import com.moss.javatest.book.dto.category.AddCategoryResult;
import com.moss.javatest.book.dto.category.CategoryDto;
import com.moss.javatest.book.dto.category.UpdateCategoryCommand;
import com.moss.javatest.book.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 분류 Controller
 */
@RequestMapping("v1.0/categories")
@RestController
public class CategoryController {
    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    /**
     * 추가
     * @param command 추가 Command
     */
    @PostMapping
    public AddCategoryResult add(@RequestBody AddCategoryCommand command) {
        return service.add(command);
    }

    /**
     * 조회
     * @param id 분류 Id
     * @return 분류
     */
    @GetMapping("{id}")
    public CategoryDto get(@PathVariable Integer id) {
        return service.get(id);
    }

    /**
     * 목록 조회
     * @return 분류 목록
     */
    @GetMapping
    public List<CategoryDto> list() {
        return service.list();
    }

    /**
     * 분류 수정
     * @param id Id
     * @param command 수정 Command
     */
    @PutMapping("{id}")
    public void update(@PathVariable Integer id, @RequestBody UpdateCategoryCommand command) {
        command.setId(id);
        service.update(command);
    }

    /**
     * 분류 삭제
     * @param id Id
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable Integer id){
        service.delete(id);
    }
}
