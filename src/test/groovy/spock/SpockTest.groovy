package spock


import spock.lang.Specification

import java.math.RoundingMode

import static spock.NegativeNumberNotAllowException.*

/**
 * Groovy + Spock 으로 작성해보는 간단한 테스트
 * See <a href="https://woowabros.github.io/study/2018/03/01/spock-test.html">Spock으로 테스트코드를 짜보자</a>
 */
class SpockTest extends Specification {
    def "금액의 퍼센트 계산 결과의 소수점 버림을 검증한다"() {
        given:
        RoundingMode 소수점버림 = RoundingMode.DOWN

        when:
        def calculate = Calculator.calculate(10000L, 0.1f, 소수점버림)

        then:
        calculate == 10L
    }

    def "여러 금액의 퍼센트 계산 결과값의 소수점 버림을 검증한다"() {
        given:
        RoundingMode 소수점버림 = RoundingMode.DOWN

        expect:
        Calculator.calculate(amount, rate, 소수점버림) == result

        where:
        amount | rate  | result
        10000L | 0.1f  | 10L
        2799L  | 0.2f  | 5L
        159L   | 0.15f | 0L
        2299L  | 0.15f | 3L
    }

    def "음수가 들어오면 예외가 발생하는지 확인해보자"() {
        given:
        RoundingMode 소수점버림 = RoundingMode.DOWN
        def amount = -10000L


        when:
        Calculator.calculate(amount, 0.1f, 소수점버림)

        then:

        then:
        def e = thrown(NegativeNumberNotAllowException.class)
        e.message == "음수는 계산할 수 없습니다."
        e.value == amount

    }

    def "주문금액의 소수점 버림을 검증한다."() {
        given:
        RoundingMode 소수점버림 = RoundingMode.DOWN
        def orderSheet = Mock(OrderSheet.class)

        when:
        long amount = orderSheet.getTotalOrderAmount()

        then:
        orderSheet.getTotalOrderAmount() >> 10000L
        amount == 10000L
        10L == Calculator.calculate(amount, 0.1f, 소수점버림)
    }

    def "1번 호출횟수 확인"() {
        given:
        RoundingMode 소수점버림 = RoundingMode.DOWN
        def orderSheet = Mock(OrderSheet.class)

        when:
        long amount = orderSheet.getTotalOrderAmount()

        then:
        1 * orderSheet.getTotalOrderAmount()

    }

    def "1번 이상 호출횟수 확인"() {
        given:
        RoundingMode 소수점버림 = RoundingMode.DOWN
        def orderSheet = Mock(OrderSheet.class)

        when:
        long amount = orderSheet.getTotalOrderAmount()

        then:
        (1.._) * orderSheet.getTotalOrderAmount()

    }

    def "2번 이하 호출횟수 확인"() {
        given:
        RoundingMode 소수점버림 = RoundingMode.DOWN
        def orderSheet = Mock(OrderSheet.class)

        when:
        long amount = orderSheet.getTotalOrderAmount()

        then:
        (_..2) * orderSheet.getTotalOrderAmount()

    }


}
