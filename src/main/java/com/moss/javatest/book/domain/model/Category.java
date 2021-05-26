package com.moss.javatest.book.domain.model;

import lombok.*;

import javax.persistence.*;

/**
 * 분류
 * Id를 DB auto increase를 사용하는 경우
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@EqualsAndHashCode
@ToString
//----
@Entity
@Table(name = "Categories")
public class Category {
    // 분류 Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    // 이름
    @Setter
    private String name;

    public static class Constraint {
        public static final int NameMaxLength = 50;
    }
}
