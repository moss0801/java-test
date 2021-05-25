package com.moss.javatest

import com.moss.javatest.shared.userinterface.HomeController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

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
