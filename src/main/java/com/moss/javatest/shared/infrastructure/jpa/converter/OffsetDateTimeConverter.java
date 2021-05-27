package com.moss.javatest.shared.infrastructure.jpa.converter;

import com.moss.javatest.shared.util.TimeUtils;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

/**
 * OffsetDateTime Converter
 */
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, Timestamp> {
    @Override
    public Timestamp convertToDatabaseColumn(OffsetDateTime attribute) {
        if (null == attribute) {
            return null;
        }
        Timestamp timestamp = Timestamp.valueOf(attribute.toLocalDateTime());
        return timestamp;
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(Timestamp dbData) {
        if (null == dbData) {
            return null;
        }
        var offsetDateTime = TimeUtils.from(dbData.toLocalDateTime());
        return offsetDateTime;
    }
}
