package com.moss.javatest.book.service;

import com.moss.javatest.book.domain.model.Category;
import com.moss.javatest.book.domain.repository.CategoryRepository;
import com.moss.javatest.book.dto.category.AddCategoryCommand;
import com.moss.javatest.book.dto.category.AddCategoryResult;
import com.moss.javatest.book.dto.category.CategoryDto;
import com.moss.javatest.book.dto.category.UpdateCategoryCommand;
import com.moss.javatest.shared.dto.DtoAssembler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 분류 서비스
 */
@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    /**
     * 추가
     * @param command 분류 추가 Command
     */
    @Transactional
    public AddCategoryResult add(AddCategoryCommand command) {
        // 생성
        var category = Category.builder().name(command.getName()).build();

        // 저장
        repository.save(category);

        AddCategoryResult reuslt = new AddCategoryResult();
        reuslt.setId(category.getId());
        return reuslt;
    }

    /**
     * 조회
     * @param id 분류 Id
     * @return 분류 Dto
     */
    @Transactional(readOnly = true)
    public CategoryDto get(Integer id) {
        return DtoAssembler.to(repository.findById(id).get(), CategoryDto.class);
    }

    @Transactional(readOnly = true)
    public boolean exist(Integer id) {
        return repository.existsById(id);
    }

    /**
     * 목록 조회
     * @return 분류 목록
     */
    @Transactional(readOnly = true)
    public List<CategoryDto> list() {
        return DtoAssembler.to(repository.findAll(), CategoryDto.class);
    }

    /**
     * 수정
     * @param command 수정 command
     */
    @Transactional
    public void update(UpdateCategoryCommand command) {
        // 존재 확인
        var categoryOptional = repository.findById(command.getId());
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("category is not exist.");
        }

        // 수정
        var categoty = categoryOptional.get();
        categoty.setName(command.getName());
    }

    @Transactional
    public void delete(Integer id) {
        // 존재확인
        var categoryOptional = repository.findById(id);
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("category is not exist.");
        }
        
        // TODO: 사용여부 확인

        // 삭제
        repository.deleteById(id);
    }
    
}
