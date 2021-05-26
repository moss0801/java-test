package com.moss.javatest.book.domain.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * BookId
 */
@Value
@AllArgsConstructor(staticName = "of")
//----
@Embeddable
public class BookId implements Serializable {
    // Id
    private String id;

    // For JPA
    protected BookId() {
        this.id = null;
    }
}
