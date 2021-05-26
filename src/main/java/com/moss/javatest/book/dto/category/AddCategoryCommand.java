package com.moss.javatest.book.dto.category;

import lombok.Data;

/**
 * 분류 추가 Command
 */
@Data
public class AddCategoryCommand {
    // 이름
    private String name;
}
