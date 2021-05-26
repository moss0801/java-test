package com.moss.javatest.shared.dto

import spock.lang.Specification

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

class DtoAssemblerTest extends Specification {

    def "to 테스트"() {
        given:
        var model = new SimpleModel()
        var a = 1;
        var b = "b"
        var now = OffsetDateTime.now();
        model.setA(a)
        model.setB(b)
        model.setTime(now)


        when:
        SimpleDto dto = DtoAssembler.to(model, SimpleDto)

        then:
        a == dto.getA()
        b == dto.getB()
        now.toEpochSecond() != dto.getEpochSecond()
        0 == dto.getEpochSecond()
    }

    def "to 테스트 with modifier"() {
        given:
        var model = new SimpleModel()
        var a = 1;
        var b = "b"
        var now = OffsetDateTime.now();
        model.setA(a)
        model.setB(b)
        model.setTime(now)


        when:
        SimpleDto dto = DtoAssembler.to(model, SimpleDto, (d, m) -> {
            d.setEpochSecond(m.getTime().toEpochSecond())
        })

        then:
        a == dto.getA()
        b == dto.getB()
        now.toEpochSecond() == dto.getEpochSecond()
    }

    def "List to 테스트"() {
        given:
        var model1 = new SimpleModel()
        var a1 = 1;
        var b1 = "b"
        var now1 = OffsetDateTime.now();
        model1.setA(a1)
        model1.setB(b1)
        model1.setTime(now1)

        var model2 = new SimpleModel()
        var a2 = 3;
        var b2 = "d"
        var now2 = OffsetDateTime.now();
        model2.setA(a2)
        model2.setB(b2)
        model2.setTime(now2)

        var models = new ArrayList<SimpleModel>();
        models.add(model1);
        models.add(model2);

        when:
        List<SimpleDto> dtos = DtoAssembler.to(models, SimpleDto)

        then:
        2 == dtos.size()
        var dto1 = dtos.get(0)
        a1 == dto1.getA()
        b1 == dto1.getB()
        now1.toEpochSecond() != dto1.getEpochSecond()
        0 == dto1.getEpochSecond()

        var dto2 = dtos.get(1)
        a2 == dto2.getA()
        b2 == dto2.getB()
        now2.toEpochSecond() != dto2.getEpochSecond()
        0 == dto2.getEpochSecond()
    }

    def "List to 테스트 with modifier"() {
        given:
        var model1 = new SimpleModel()
        var a1 = 1;
        var b1 = "b"
        var now1 = OffsetDateTime.now();
        model1.setA(a1)
        model1.setB(b1)
        model1.setTime(now1)

        var model2 = new SimpleModel()
        var a2 = 3;
        var b2 = "d"
        var now2 = OffsetDateTime.now();
        model2.setA(a2)
        model2.setB(b2)
        model2.setTime(now2)

        var models = new ArrayList<SimpleModel>();
        models.add(model1);
        models.add(model2);

        when:
        List<SimpleDto> dtos = DtoAssembler.to(models, SimpleDto, (d, m) -> {
            d.setEpochSecond(m.getTime().toEpochSecond())
        })

        then:
        2 == dtos.size()
        var dto1 = dtos.get(0)
        a1 == dto1.getA()
        b1 == dto1.getB()
        now1.toEpochSecond() == dto1.getEpochSecond()

        var dto2 = dtos.get(1)
        a2 == dto2.getA()
        b2 == dto2.getB()
        now2.toEpochSecond() == dto2.getEpochSecond()
    }

    def "from 테스트"() {
        given:
        var dto = new SimpleDto()
        var a = 1;
        var b = "b"
        var now = OffsetDateTime.now();
        dto.setA(a)
        dto.setB(b)
        dto.setEpochSecond(now.toEpochSecond())

        when:
        var model = DtoAssembler.from(dto, SimpleModel)

        then:
        a == model.getA()
        b == model.getB()
        null == model.getTime()
    }

    def "from 테스트 with modifier"() {
        given:
        var dto = new SimpleDto()
        var a = 1;
        var b = "b"
        var now = OffsetDateTime.now();
        dto.setA(a)
        dto.setB(b)
        dto.setEpochSecond(now.toEpochSecond())

        when:
        SimpleModel model = DtoAssembler.from(dto, SimpleModel, (m, d) -> {
            var time = OffsetDateTime.ofInstant(Instant.ofEpochSecond(d.getEpochSecond()), ZoneId.systemDefault())
            m.setTime(time)
        })

        then:
        a == model.getA()
        b == model.getB()
        now.toEpochSecond() == model.getTime().toEpochSecond()
    }

    def "List from 테스트"() {
        given:
        var dto1 = new SimpleDto()
        var a1 = 1;
        var b1 = "b"
        var now1 = OffsetDateTime.now();
        dto1.setA(a1)
        dto1.setB(b1)
        dto1.setEpochSecond(now1.toEpochSecond())

        var dto2 = new SimpleDto()
        var a2 = 3;
        var b2 = "d"
        var now2 = OffsetDateTime.now();
        dto2.setA(a2)
        dto2.setB(b2)
        dto2.setEpochSecond(now2.toEpochSecond())

        var dtos = new ArrayList<SimpleDto>();
        dtos.add(dto1);
        dtos.add(dto2);

        when:
        List<SimpleModel> models = DtoAssembler.from(dtos, SimpleModel)

        then:
        2 == models.size()
        var model1 = models.get(0)
        a1 == model1.getA()
        b1 == model1.getB()
        null == model1.getTime()

        var model2 = models.get(1)
        a2 == model2.getA()
        b2 == model2.getB()
        null == model2.getTime()
    }

    def "List from 테스트 with modifier"() {
        given:
        var dto1 = new SimpleDto()
        var a1 = 1;
        var b1 = "b"
        var now1 = OffsetDateTime.now();
        dto1.setA(a1)
        dto1.setB(b1)
        dto1.setEpochSecond(now1.toEpochSecond())

        var dto2 = new SimpleDto()
        var a2 = 3;
        var b2 = "d"
        var now2 = OffsetDateTime.now();
        dto2.setA(a2)
        dto2.setB(b2)
        dto2.setEpochSecond(now2.toEpochSecond())

        var dtos = new ArrayList<SimpleDto>();
        dtos.add(dto1);
        dtos.add(dto2);

        when:
        List<SimpleModel> models = DtoAssembler.from(dtos, SimpleModel, (m, d) -> {
            var time = OffsetDateTime.ofInstant(Instant.ofEpochSecond(d.getEpochSecond()), ZoneId.systemDefault())
            m.setTime(time)
        })

        then:
        2 == models.size()
        var model1 = models.get(0)
        a1 == model1.getA()
        b1 == model1.getB()
        now1.toEpochSecond() == model1.getTime().toEpochSecond()

        var model2 = models.get(1)
        a2 == model2.getA()
        b2 == model2.getB()
        now2.toEpochSecond() == model2.getTime().toEpochSecond()
    }
}
