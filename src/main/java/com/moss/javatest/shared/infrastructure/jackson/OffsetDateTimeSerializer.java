package com.moss.javatest.shared.infrastructure.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.moss.javatest.shared.util.TimeUtils;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * OffsetDateTime 객체를 ISO 8601 문자열로 변환
 */
public class OffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
    @Override
    public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(TimeUtils.print(value));
    }
}
