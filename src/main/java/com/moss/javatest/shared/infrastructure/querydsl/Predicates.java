package com.moss.javatest.shared.infrastructure.querydsl;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * QueryDSL BooleanExpression을 SQL과 유사한 함수형 형태로 작성하기 위한 Helper Class
 */
public class Predicates {
    // expression을 mainExpression에 적용할 Operation
    protected Ops ops;
    // 최종 BooleanExpression
    protected BooleanExpression mainExpression;
    // 적용하는 BooleanExpresion
    protected BooleanExpression expression;
    // 직전 Predicates
    protected Predicates previous;

    /**
     * start()만 사용하는 생성자
     */
    private Predicates() { }

    /**
     * 다음 단계의 Predicates 생성
     * @param previous 직전 BooleanExpression까지 적용한 Predicates
     * @param mainExpression 직전 BooleanExpression까지 적용된 최종 BooleanExpression
     */
    private Predicates(Predicates previous, BooleanExpression mainExpression) {
        this.previous = previous;
        this.mainExpression = mainExpression;
    }

    //// (s) Ops

    /**
     * And Operation 설정
     */
    public Predicates and() {
        return setOps(Ops.AND);
    }

    /**
     * Or Operation 설정
     */
    public Predicates or() {
        return setOps(Ops.OR);
    }

    /**
     * Operations 설정
     * 한번 호출 되었다면 Predicates apply(BooleanExpression) 호출 후, 호출 가능
     * @param ops operation
     * @return operation 반영된 Predicates 반환
     */
    private Predicates setOps(Ops ops) {
        if (null == ops) {
            throw new IllegalArgumentException("ops must not be null.");
        }
        if (null != this.ops) {
            throw new IllegalArgumentException("ops already exists.");
        }
        this.ops = ops;
        return this;
    }

    //// (e) Ops

    //// (s) optional, required

    /**
     * 값의 유효성 확인
     * @param value 값
     * @return 값의 유효성 여부
     */
    private boolean isAvailable(Object value) {
        boolean available;
        if (null == value) {
            available = false;
        } else if (value instanceof String) {
            available = StringUtils.hasText((String) value);
        } else if (value instanceof Collection) {
            available = null != value && ((Collection)value).size() > 0;
        } else {
            available = null != value;
        }
        return available;
    }

    /**
     * 값이 존재하는 경우 적용, 여러 값을 이용해 임의의 BooleanExpresion 생성이 필요한 경우
     * @param calculator 값들을 이용해 BooleanExpression을 계산할 계산자
     * @param values 게산에 사용할 값들
     * @return ExpressionCalculator에 의해 계산된 BooleanExpression을 적용한 Predicates
     */
    public Predicates optional(ExpressionCalculator calculator, Object... values) {
        // 유효성 체크
        for (Object value : values) {
            boolean available = isAvailable(value);
            if (!available) {
                return apply(null);
            }
        }
        return optional(calculator.calculate());
    }

    /**
     * BooleanExpression이 필수가 아닌 경우
     * @param expression 적용할 BooleanExpression
     * @return booleanExpression을 적용한 결과
     */
    public Predicates optional(BooleanExpression expression) {
        return apply(expression);
    }

    /**
     * 값이 존재하는 경우 적용, 입력값이 1개 필요한 BooleanExpression
     * @param func Function
     * @param value 첫번째 값
     * @param <T> 첫번째 값의 타입
     * @return BooleanExpression을 적용한 Predicates
     */
    public <T> Predicates optional(Function<T, BooleanExpression> func, T value) {
        if (!isAvailable(value)) {
            return apply(null);
        }
        return apply(func.apply(value));
    }

    /**
     * 값이 존재하는 경우 적용, 입력값이 2개 필요한 BooleanExpression
     * @param func BiFunction
     * @param first 첫번째 값
     * @param second 두번째 값
     * @param <T> 첫번째 값의 타입
     * @param <U> 두번째 값의 타입
     * @return BooleanExpression을 적용한 Predicates
     */
    public <T, U> Predicates optional(BiFunction<T, U, BooleanExpression> func, T first, U second) {
        if (!isAvailable(first) || !isAvailable(second)) {
            return apply(null);
        }
        return apply(func.apply(first, second));
    }

    /**
     * Expression이 필수인 경우
     * @param expression 적용할 BooleanExpression
     * @return BooleanExpression을 적용한 Predicates
     */
    public Predicates required(BooleanExpression expression) {
        if (null == expression) {
            throw new IllegalArgumentException("expression is required");
        }
        return apply(expression);
    }

    /**
     * 값이 필수인 경우, 입력값이 1개 필요한 BooleanExpression
     * @param func Function
     * @param value 첫번째 값
     * @param <T> 첫번째 값 타입
     * @return BooleanExpression을 적용한 Predicates
     */
    public <T> Predicates required(Function<T, BooleanExpression> func, T value) {
        return apply(func.apply(value));
    }

    /**
     * 값이 필수인 경우, 입력값이 2개 필요한 BooleanExpression
     * @param func BiFunction
     * @param first 첫번째 값
     * @param second 두번째 값
     * @param <T> 첫번째 값의 타입
     * @param <U> 두번째 값의 타입
     * @return BooleanExpression을 적용한 Predicates
     */
    public <T, U> Predicates required(BiFunction<T, U, BooleanExpression> func, T first, U second) {
        return apply(func.apply(first, second));
    }

    /**
     * BooleanExpression을 적용
     * BooleanExpression을 적용할 Predicates setOps(ops)가 먼저 호출 되어야 한다.
     * @param expression MainExpression에 Operaion함께 적용할 BooleanExpression
     * @return MainExpression에 Operaion과 함께 BooleanExpression을 적용한 Predicates
     */
    private Predicates apply(BooleanExpression expression) {
        if (null == expression) {
            // expression이 null인 경우 ops를 초기화
            this.ops = null;
            return this;
        }

        this.expression = expression;
        if (this.previous == null) {
            // 앞에 Predicates가 없는 경우[처음]
            this.mainExpression = this.expression;
            return new Predicates(this, this.mainExpression);
        }

        BooleanExpression mainExpression = null;
        // BooleanExpression을 MainExpression에 Operation과 함께 적용
        switch (this.ops) {
            case AND: mainExpression = this.mainExpression.and(this.expression); break;
            case OR: mainExpression = this.mainExpression.or(this.expression); break;
            default: throw new IllegalArgumentException("invalid operation '" + this.ops + "'");
        }

        this.mainExpression = mainExpression;
        return new Predicates(this, this.mainExpression);
    }

    //// (e) optional, required

    //// (s) loop

    /**
     * 목록 처리
     * @param items 목록
     * @param joinOps 목록 아이템간 Join 연산
     * @param handler 목록 아이템 처리자, 각 아이템 값에 대한 Predicates를 생성
     * @return 목록이 처리된 Predicates
     */
    public Predicates loop(Collection<?> items, JoinOps joinOps, LoopItemHandler handler) {
        // 유효성 체크
        if (!isAvailable(items)) {
            return new Predicates(this, this.mainExpression);
        }

        // 목록 아이템을 처리자로 처리
        List<Predicates> result = Lists.newArrayList();
        for (Object item : items) {
            result.add(handler.each(Predicates.start(), item));
        }

        BooleanExpression loopResult = null;
        for (Predicates predicates : result) {
            BooleanExpression itemExpression = null;
            if (null != predicates) {
                // 각 아이템의 Predicates로 부터 itemExpression(BooleanExpression) 반환
                itemExpression = predicates.end();
            }
            if (null == itemExpression) {
                // itemExpresion이 존재하지 않으면 통과
                continue;
            }
            if (null == loopResult) {
                // 첫 유효한 itemExpression인 경우 LoopResult로 설정
                loopResult = itemExpression;
                continue;
            }

            // JoinOps에 따라 itemExpression을 loopResult에 Join
            switch (joinOps) {
                case And: loopResult = loopResult.and(itemExpression); break;
                case Or: loopResult = loopResult.or(itemExpression); break;
                default: throw new IllegalArgumentException("invalid join ops '" + joinOps + "'");
            }
        }

        // 목록 처리 결과 적용
        return apply(loopResult);
    }

    //// (e) loop

    //// (s) brace

    /**
     * 괄호 () 계산
     * @param braceExpression 괄호 내부 표현식
     * @param values 괄호내 사용할 값들
     * @return 괄호 처리 적용한 Predicates
     */
    public Predicates brace(BraceExpression braceExpression, Object... values) {
        // 유효성 체크
        for (Object value : values) {
            boolean available = isAvailable(value);
            if (!available) {
                return apply(null);
            }
        }

        var result = braceExpression.brace(Predicates.start());
        return apply(result.end());
    }

    //// (e) brace

    //// (s) Start, End

    /**
     * Predicates 시작
     */
    public static Predicates start() {
        return new Predicates().and();
    }

    /**
     * Predicates 종료
     * @return 최종 BooleanExpression
     */
    public BooleanExpression end() {
        return mainExpression;
    }

    //// (e) Start, End


    /**
     * 여러값을 이용한 BooleanExpression 계산
     */
    @FunctionalInterface
    public interface ExpressionCalculator {
        BooleanExpression calculate();
    }

    /**
     * 목록 아이템을 Predicates로 반환하는 핸들러
     */
    @FunctionalInterface
    public interface LoopItemHandler {
        Predicates each(Predicates predicates, Object value);
    }

    /**
     * 괄호() 를 표현하기 위한 Expression
     */
    @FunctionalInterface
    public interface BraceExpression {
        Predicates brace(Predicates predicates);
    }

    public enum JoinOps {
        And,
        Or,
        ;
    }
}
