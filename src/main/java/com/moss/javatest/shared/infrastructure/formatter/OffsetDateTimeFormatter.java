package com.moss.javatest.shared.infrastructure.formatter;

import com.moss.javatest.shared.util.TimeUtils;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.Locale;

/**
 * OffsetDateTime 객체를 ISO 8601로 처리하기 위한 Formatter
 */
public class OffsetDateTimeFormatter implements Formatter<OffsetDateTime> {
    @Override
    public OffsetDateTime parse(String text, Locale locale) throws ParseException {
        return TimeUtils.parse(text);
    }

    @Override
    public String print(OffsetDateTime object, Locale locale) {
        return TimeUtils.print(object);
    }
}
