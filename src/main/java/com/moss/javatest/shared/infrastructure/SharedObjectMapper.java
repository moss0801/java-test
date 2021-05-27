package com.moss.javatest.shared.infrastructure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.moss.javatest.shared.infrastructure.jackson.OffsetDateTimeDeserializer;
import com.moss.javatest.shared.infrastructure.jackson.OffsetDateTimeSerializer;

import java.time.OffsetDateTime;

/**
 * Custom ObjectMapper
 */
public class SharedObjectMapper extends ObjectMapper {
    public SharedObjectMapper() {
        super();

        // json의 속성이 변경되었을 때 에러 발생을 막기 위함
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // null값은 결과에 포함되지 않도록 함
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Custom Serializer, Deserializer
        SimpleModule module = new SimpleModule("customModule");

        // OffsetDateTime
        module.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        module.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());

        this.registerModule(module);
    }
}
