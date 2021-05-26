package com.moss.javatest.book.domain.repository;

import com.moss.javatest.book.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 분류 Repository
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
