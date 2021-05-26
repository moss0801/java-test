package com.moss.javatest.shared.infrastructure.querydsl;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Queue;

/**
 * Querydsl 사용을 위한 helper 함수를 추가한 QuerydslRepositorySupport
 */
@Slf4j
public class SharedQuerydslRepositorySupport extends QuerydslRepositorySupport {
    private Class<? extends EntityPathBase> qDomainClass;

    private Expression[] expressions;

    private void setExpressions(Expression[] expressions) {
        this.expressions = expressions;
    }

    protected Expression[] getExpressions() {
        return this.expressions;
    }

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     * @param domainClass must not be {@literal null}.
     */
    public SharedQuerydslRepositorySupport(
            Class<?> domainClass, Class<? extends EntityPathBase> qDomainClass, EntityPathBase qDomain) {
        super(domainClass);
        this.qDomainClass = qDomainClass;
        this.expressions = toArray(getExpressions(qDomain));
    }

    /**
     * Q Domain 클래스로 부터 Expression 목록 추출
     * @param obj Q Domin 클래스
     * @return expression 목록
     */
    protected List<Expression> getExpressions(EntityPathBase obj) {
        List<Expression> expressions = Lists.newArrayList();
        Queue queue = Queues.newArrayDeque();
        queue.offer(obj);
        while(!queue.isEmpty()) {
            Object target = queue.poll();
            Class<?> clz = target.getClass();
            for (Field field : clz.getDeclaredFields()) {
                // 정의된 field 확인
                if (Modifier.isStatic(field.getModifiers())) {
                    // static 필드는 무시
                    continue;
                }
                if (BeanPath.class.isAssignableFrom(field.getType())) {
                    // Type인 경우 Queue 추가
                    try {
                        queue.offer(field.get(target));
                    } catch (IllegalAccessException e) {
                        log.error("fail to get field", e);
                    }
                } else if (Path.class.isAssignableFrom(field.getType())) {
                    // Expression 추가
                    try {
                        expressions.add((Expression) field.get(target));
                    } catch (IllegalAccessException e) {
                        log.error("fail to get field", e);
                    }
                }
            }
        }
        
        return expressions;
    }

    /**
     * page, size로 offset 계산
     * @param page 1부터 시작하는 page
     * @param size page당 항목 수
     * @return offset
     */
    protected int offset(int page, int size) {
        return (page - 1)*size;
    }

    /**
     * keyword 양쪽 like 처리
     * @param keyword 키워드
     * @return 키워드 양쪽 like 처리(%키워드%)
     */
    protected String likeSide(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return keyword;
        }
        return "%" + keyword + "%";
    }

    /**
     * List<Expression>을 Expression[]로 변환
     * @param expressions expression 목록
     * @return expression 배열
     */
    protected Expression[] toArray(List<Expression> expressions) {
        if (null == expressions) {
            return null;
        }
        return expressions.stream().toArray(Expression[]::new);
    }

    /**
     * Select절 SubQuery 처리
     * @param expression
     * @param <T>
     * @return
     */
    protected <T> JPQLQuery<T> subSelect(Expression<T> expression) {
        return JPAExpressions.select(expression);
    }

    /**
     * Select Dto
     * @param type 반환값 저장할 Type
     * @param expressions 조회할 값의 Expression 목록
     * @param <T> 반환값 Type
     * @return JPQLQuery
     */
    protected <T> JPQLQuery<T> selectDto(Class<? extends T> type, Expression<?>... expressions) {
        return getQuerydsl().createQuery().select(Projections.fields(type, expressions));
    }

    /**
     * as 처리
     * @param source source Expression
     * @param alias alias
     * @param <D>
     * @return alias 처리된 Expression
     */
    protected <D> Expression<D> as(Expression<D> source, String alias) {
        return ExpressionUtils.as(source, alias);
    }
}
