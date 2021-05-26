package com.moss.javatest.book.dto.category;

import lombok.Data;

/**
 * 분류 수정 Command
 */
@Data
public class UpdateCategoryCommand {
    // Id
    private Integer id;

    // 이름
    private String name;
}
