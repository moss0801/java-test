# 개요
Java8 이후 time package를 이용한 날짜 시간 처리

# 기준
* Java 내 에서는 OffsetDateTime을 이용 (아직은 ZonedDateTime대신 OffsetDateTime로도 필요한 로직을 구현)
* Java 외 에서는 ISO 8601 포맷을 이용
   * yyyy-MM-ddTHH:mm:ss.SSSZZZ (예) 2021-05-11T00:00:00.000+09:00)
   * 예전 Java를 사용으로 yyyy-MM-dd HH:mm:ss.SSS 을 사용하는 곳이 남아있기에 대응 필요
    
# TimeUtils
다양한 상황에서 OffsetDateTime, ISO 8601 로 시간을 처리하기 위한 유틸 모음

* ISO 8601를 OffsetDateTime으로 파싱
* OffsetDateTime을 ISO 8601로 출력
* 등등

## OffsetDateTimeFormatter
Spring @ModelAttribute, @RequestParam 에 대응하기 위한 Formatter

### Spring Configuration
```
@Configuration
public class AppConfiguration implements WebMvcConfigurer {
    ...
    @Override
    public void addFormatters(FormatterRegistry registry) {
        // OffsetDateTime
        registry.addFormatter(new OffsetDateTimeFormatter());
    }
    ...
}
```

## OffsetDateTimeSerializer, OffsetDateTimeDeserializer, SharedObjectMapper
Spring @RequestBody 에 대응하기 위한 Jackson2 Serializer, Deserializer 

### ShareObjectMapper
OffsetDateTimeSerializer, OffsetDateTimeDeserializer를 objectMapper에 추가

```
public class SharedObjectMapper extends ObjectMapper {
    public SharedObjectMapper() {
        super();
        ...

        // Custom Serializer, Deserializer
        SimpleModule module = new SimpleModule("customModule");

        // OffsetDateTime
        module.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        module.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());

        this.registerModule(module);
    }
}

```

### Srping Configuration
MessageConverter 재등록  
JSON만 지원하는 API만 지원할 경우 나머지 MessageConverter는 불필요
```
@Configuration
public class AppConfiguration implements WebMvcConfigurer {
    ...
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.clear();
        converters.add(new MappingJackson2HttpMessageConverter(new SharedObjectMapper()));
    }
    ...
}
```

## OffsetDateTimeConverter
JPA사용간 OffsetDateTime이 정상적으로 처리되지 않는 경우를 대비하여 사용  
@Converter(autoApply = true) 를 이용해서 전체 적용

```
@Converter(autoApply = true)
public class AppOffsetDateTimeConverter extends OffsetDateTimeConverter {
}
```
