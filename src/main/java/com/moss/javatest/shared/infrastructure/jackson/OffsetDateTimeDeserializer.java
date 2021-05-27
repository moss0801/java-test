package com.moss.javatest.shared.infrastructure.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.moss.javatest.shared.util.TimeUtils;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * ISO 8601을 OffsetDateTime으로 변환
 */
public class OffsetDateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return TimeUtils.parse(p.getValueAsString());
    }
}
