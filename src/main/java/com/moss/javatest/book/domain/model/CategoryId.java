package com.moss.javatest.book.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Category Id
 */
@Value
@AllArgsConstructor(staticName = "of")
//----
@Embeddable
public class CategoryId implements Serializable {
    private Integer id;

    // For JPA
    protected CategoryId() {
        this.id = null;
    }
}
