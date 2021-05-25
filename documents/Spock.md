
# Gradle plugin 및 종속성 추가
Spock은 groovy로 작성하기에 groovy pluin 및 spock-spring 종속성 추가
 
build.gradle
```
plugins {
	id 'groovy'
}

dependencies {
	testImplementation 'org.spockframework:spock-spring:2.0-groovy-2.5'
}
```

# test 하위 groovy 폴더 생성
intellij 기준으로 폴더가 녹색으로 변경되어야 정상인식 된 것이다.

# 테스트 class, Calculator
```
public class Calculator {
    public static long calculate(long amount , float rate, RoundingMode roundingMode) {
        if (amount < 0) {
            throw new NegativeNumberNotAllowException(amount);
        }
        return BigDecimal.valueOf(amount * rate * 0.01)
                .setScale(0, roundingMode ).longValue();
    }
}
```

# 테스트 작성
Test클래스는 Specitifcation을 상속받은 후 테스트를 작성한다.

```
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
```

# Spring Test

gradle.build
```
plugins {
	id 'groovy'
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.spockframework:spock-spring:2.0-groovy-2.5'
}
```

HomeController
```
@RestController
public class HomeController {
    @GetMapping
    public String index() {
        return "Hello, World!";
    }
}

```

## Loading ApplicationContext
@SpringBootTest를 이용하여 ApplicationContext가 Loading되어 Bean이 생성되었음을 테스트
```
@SpringBootTest
class SpringLoadingApplicationContextTest extends Specification {
    @Autowired
    private HomeController homeController

    def "when context is loaded then all expected beans are created"() {
        expect: "the HomeController is created"
        homeController
        "Hello, World!" == homeController.index()
    }
}
```

## Using WebMvcTest
@AUtoConfigurureMockMvc, @WebMvcTest를 이용해서 호출 및 결과 테스트

```
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@AutoConfigureMockMvc
@WebMvcTest
class WebControllerTest extends Specification {

    @Autowired
    private MockMvc mvc

    def "when get is performed then the response has status 200 and content is 'Hello world!'"() {
        expect: "Status is 200 and response is 'Hello, world!'"

        mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andReturn()
            .response
            .contentAsString == "Hello, World!"
    }
}
```

# Reference

* [Spock으로 테스트코드를 짜보자](https://woowabros.github.io/study/2018/03/01/spock-test.html)
* [Testing with Spring and Spock](https://www.baeldung.com/spring-spock-testing)

 