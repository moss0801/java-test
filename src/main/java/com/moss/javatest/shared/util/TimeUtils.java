package com.moss.javatest.shared.util;

import org.apache.tomcat.jni.Local;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Date Time 관련 Util 모음<
 * OffsetDateTime 기준 처리
 * ZoneOffset은 기본 ZoneOffset(OffsetDateTime.now().getOffset())을 사용
 * 문자열 표현은 ISO 8601을 기준으로 처리
 *
 * 출력 및 파싱 패턴
 * ISO 8601     : yyyy-MM-ddTHH:mm:ss.SSSXXX
 * LocalDateTime: yyyy-MM-ddTHH:mm:ss.SSS
 */
public class TimeUtils {
    public static final ZoneOffset DefaultZoneOffset = OffsetDateTime.now().getOffset();
    public static final DateTimeFormatter ISO8601_PRINT_FORMATTER;
    public static final DateTimeFormatter ISO8601_PRINT_FORMATTER_NO_MILLIS;
    public static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER;
    public static final LocalTime LOCAL_TIME_END_OF_DAY = LocalTime.ofNanoOfDay(86399999000000L);
    public static final LocalTime LOCAL_TIME_END_OF_DAY_NO_NANO = LocalTime.ofNanoOfDay(86399000000000L);

    static {
        // 1970-01-01T00:00:00.000+09:00
        ISO8601_PRINT_FORMATTER = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral('T')
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendFraction(ChronoField.MILLI_OF_SECOND, 3, 3, true)
                .appendOffsetId()
                .toFormatter();

        // 1970-01-01T00:00:00+09:00
        ISO8601_PRINT_FORMATTER_NO_MILLIS = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral('T')
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendOffsetId()
                .toFormatter();

        // 1970-01-01 00:00:00
        LOCAL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .appendLiteral(' ')
                .appendValue(ChronoField.HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
                .appendOffsetId()
                .optionalStart()
                .appendFraction(ChronoField.MILLI_OF_SECOND, 3, 3, true)
                .optionalEnd()
                .toFormatter();
    }

    /**
     * 현재 시간 조회
     * @return 현재 시간 OffsetDateTime
     */
    public static OffsetDateTime now() {
        return OffsetDateTime.now();
    }

    /**
     * 현재 시간 조회(nano 값 0)
     * millis가 0이 되고 second 까지만 표시
     * @return 초까지 값이 있는 현재 시간 OffsetDateTime
     */
    public static OffsetDateTime nowNoNano() {
        return now().withNano(0);
    }

    /**
     * 기본 ZoneOffset 반환
     * @return 기본 ZoneOffset
     */
    public static ZoneOffset zoneOffset() {
        return DefaultZoneOffset;
    }

    /**
     * Instant를 기본 ZoneOffset 기준의 OffsetDateTime으로 변환
     * @param instant Instant
     * @return Instant와 기본 ZoneOffset을 결합한 OffsetDateTime
     */
    public static OffsetDateTime from(Instant instant) {
        return OffsetDateTime.ofInstant(instant, zoneOffset());
    }

    /**
     * epochMilli를 기준 ZoneOffset 기준의 OffsetDateTime으로 변화
     * @param epochMilli epochMilli
     * @return epochMilli와 기본 ZoneOffset을 결합한 OffsetDateTime
     */
    public static OffsetDateTime from(long epochMilli) {
        return from(Instant.ofEpochMilli(epochMilli));
    }

    /**
     * localDateTime를 기본 ZoneOffset 기준의 OffsetDateTime으로 변환
     * @param localDateTime LocalDateTime
     * @return LocalDateTime과 기본 ZoneOffset을 결합한 OffsetDateTime
     */
    public static OffsetDateTime from(LocalDateTime localDateTime) {
        return OffsetDateTime.of(localDateTime, zoneOffset());
    }

    //// (s) parse

    /**
     * ISO 8601 문자열을 기본 ZoneOffset 기준의 OffsetDateTime로 변환
     * @param iso8601 ISO 8601 문자열
     * @return ISO 8601의 ZoneOffset을 기본 ZoneOffset으로 계산한 OffsetDateTime
     */
    public static OffsetDateTime parse(String iso8601) {
        var offsetDateTime = OffsetDateTime.parse(iso8601);
        offsetDateTime = offsetDateTime.withOffsetSameInstant(zoneOffset());
        return offsetDateTime;
    }

    /**
     * ISO 8601 문자열을 Instant 객체로 반환
     * @param iso8601 ISO 8601 문자열
     * @return ISO 8601 문자열의 Instant
     */
    public static Instant parseAsInstant(String iso8601) {
        var offsetDateTime = parse(iso8601);
        return offsetDateTime.toInstant();
    }

    /**
     * ISO 8601 문자열을 기본 ZoneOffset 기준으로 파싱 후, LocalDateTime 객체를 반환
     * @param iso8601 ISO 8601 문자열
     * @return ISO 8601 문자열의 ZoneOffset 기준 LocalDateTime
     */
    public static LocalDateTime parseAsLocalDateTime(String iso8601) {
        var offsetDateTime = parse(iso8601);
        return offsetDateTime.toLocalDateTime();
    }

    /**
     * offset 정보를 포함한 value를 주어진 formatter로 파싱 후 기본 ZoneOffset 기준 OffsetDateTime으로 변환
     * @param value offset을 포함한 DateTime 문자열
     * @param formatter value를 파싱하기 위한 formatter
     * @return value를 기본 ZoneOffset으로 변환한 OffsetDateTime
     */
    public static OffsetDateTime parse(String value, DateTimeFormatter formatter) {
        var offsetDateTime = formatter.parse(value, OffsetDateTime::from);
        offsetDateTime = offsetDateTime.withOffsetSameInstant(zoneOffset());
        return offsetDateTime;
    }

    /**
     * offset 정보를 포함한 value를 주어진 pattern으로 파싱 후 기본 ZoneOffset 기준 OffsetDateTime으로 변환
     * @param value offset을 포함한 DateTime 문자열
     * @param pattern value를 파싱하기 위한 pattern
     * @return value를 기본 ZoneOffset으로 변환한 OffsetDateTime
     */
    public static OffsetDateTime parse(String value, String pattern) {
        var formatter = DateTimeFormatter.ofPattern(pattern);
        return parse(value, formatter);
    }

    /**
     * 'yyyy-MM-dd HH:mm:ss.SSS' 포맷의 문자열을 기본 ZoneOffset 기준의 OffsetDateTime으로 변환
     * @param value yyyy-MM-dd HH:mm:ss.SSS
     * @return 기본 ZoneOffset 기준 OffsetDateTime
     */
    public static OffsetDateTime parseLocalDateTime(String value) {
        var localDateTime = LocalDateTime.parse(value, LOCAL_DATE_TIME_FORMATTER);
        var offsetDateTime = from(localDateTime);
        return offsetDateTime;
    }

    /**
     * Offset 정보를 포함하지 않은 날짜,시간 문자열을 주어진 formatter로 파싱 후 기본 ZoneOffset 기준의 OffsetDateTime 반환
     * @param value Offset 정보를 포함하지 않은 날짜,시간 문자열
     * @param formatter 날짜,시간 문자열 파싱을 위한 Formatter
     * @return 기본 ZoneOffset 기준 OffsetDateTime
     */
    public static OffsetDateTime parseLocalDateTime(String value, DateTimeFormatter formatter) {
        var localDateTime = formatter.parse(value, LocalDateTime::from);
        return from(localDateTime);
    }

    /**
     * Offset 정보를 포함하지 않은 날짜,시간 문자열을 주어진 pattern으로 파싱 후 기본 ZoneOffset 기준의 OffsetDateTime 반환
     * @param value Offset 정보를 포함하지 않은 날짜,시간 문자열
     * @param pattern 날짜,시간 문자열 파싱을 위한 pattern
     * @return 기본 ZoneOffset 기준 OffsetDateTime
     */
    public static OffsetDateTime parseLocalDateTime(String value, String pattern) {
        var formatter = DateTimeFormatter.ofPattern(pattern);
        return parseLocalDateTime(value, formatter);
    }

    //// (e) parse

    //// (s) print

    /**
     * OffsetDateTime을 기본 ZoneOffset 기준 ISO 8601 포맷형태로 반환
     * @param offsetDateTime OffsetDateTime
     * @return ISO 8601
     */
    public static String print(OffsetDateTime offsetDateTime) {
        var target = offsetDateTime.withOffsetSameInstant(zoneOffset());
        return target.format(ISO8601_PRINT_FORMATTER);
    }

    /**
     * LocalDateTime을 기본 ZoneOffset 기준 ISO 8601 포맷형태로 반환
     * @param localDateTime LocalDateTime
     * @return ISO 8601
     */
    public static String print(LocalDateTime localDateTime) {
        var offsetDateTime = from(localDateTime);
        return print(offsetDateTime);
    }

    /**
     * Instant를 기본 ZoneOffset 기준 ISO 8601 포맷형태로 반환
     * @param instant Instant
     * @return ISO 8601
     */
    public static String print(Instant instant) {
        var offsetDateTime = from(instant);
        return print(offsetDateTime);
    }

    /**
     * OffsetDateTime을 기본 ZoneOffset 기준 'yyyy-MM-dd HH:mm:ss.SSS' 포맷으로 변환
     * @param offsetDateTime OffsetDateTime
     * @return yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String printLocalDateTime(OffsetDateTime offsetDateTime) {
        var converted = offsetDateTime.withOffsetSameInstant(zoneOffset());
        return converted.format(LOCAL_DATE_TIME_FORMATTER);
    }

    //// (e) print

    //// (s) epochMillis

    /**
     * OffsetDateTime의 epochMilli 값을 반환
     * @param offsetDateTime OffsetDateTime
     * @return epochMilli
     */
    public static long toEpochMilli(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toInstant().toEpochMilli();
    }

    /**
     * LocalDateTime의 기본 ZoneOffset 기준 epochMilli 값을 반환
     * @param localDateTime LocalDateTime
     * @return epochMilli
     */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        Instant instant = localDateTime.toInstant(zoneOffset());
        return instant.toEpochMilli();
    }

    //// (e) epochMillis

    //// (s) Start, End

    /**
     * LocalTime이 0시 0분 0초 0나노로 설정된 OffsetDateTime 반환
     * @param offsetDateTime OffsetDateTime
     * @return LocalTime이 0시 0분 0초 0나노 OffsetDateTime
     */
    public static OffsetDateTime startOfDay(OffsetDateTime offsetDateTime) {
        var result = OffsetDateTime.of(offsetDateTime.toLocalDate().atStartOfDay(), offsetDateTime.getOffset());
        return result;
    }

    /**
     * LocalTime이 23시 59분 59초 999000000나노로 설정된 OffsetDateTime 반환
     * @param offsetDateTime OffsetDateTime
     * @return LocalTime이 23시 59분 59초 999000000나노 OffsetDateTime
     */
    public static OffsetDateTime endOfDay(OffsetDateTime offsetDateTime) {
        var result = OffsetDateTime.of(offsetDateTime.toLocalDate(), LOCAL_TIME_END_OF_DAY, offsetDateTime.getOffset());
        return result;
    }

    /**
     * LocalTime이 23시 59분 59초 000000000나노로 설정된 OffsetDateTime 반환
     * @param offsetDateTime OffsetDateTime
     * @return LocalTime이 23시 59분 59초 000000000나노 OffsetDateTime
     */
    public static OffsetDateTime endOfDayNoNano(OffsetDateTime offsetDateTime) {
        var result = OffsetDateTime.of(offsetDateTime.toLocalDate(), LOCAL_TIME_END_OF_DAY_NO_NANO, offsetDateTime.getOffset());
        return result;
    }

    /**
     * LocalDate의 Date가 1, LocalTime이 0시 0분 0초 0나노로 설정된 OffsetDateTime 반환
     * @param offsetDateTime OffsetDateTime
     * @return LocalDate의 Date가 1, LocalTime이 0시 0분 0초 0나노로 설정된 OffsetDateTime
     */
    public static OffsetDateTime startOfMonth(OffsetDateTime offsetDateTime) {
        var localDate = offsetDateTime.toLocalDate().withDayOfMonth(1);
        var result = OffsetDateTime.of(localDate.atStartOfDay(), offsetDateTime.getOffset());
        return result;
    }

    /**
     * LocalDate의 Date가 달의 마지막일, LocalTime이 23시 59분 59초 999000000나노로 설정된 OffsetDateTime 반환
     * @param offsetDateTime OffsetDateTime
     * @return LocalDate의 Date가 달의 마지막일, LocalTime이 23시 59분 59초 999000000나노로 설정된 OffsetDateTime
     */
    public static OffsetDateTime endOfMonth(OffsetDateTime offsetDateTime) {
        var localDate = offsetDateTime.toLocalDate();
        localDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        var result = OffsetDateTime.of(localDate, LOCAL_TIME_END_OF_DAY, offsetDateTime.getOffset());
        return result;
    }

    /**
     * LocalDate의 Date가 달의 마지막일, LocalTime이 23시 59분 59초 000000000나노로 설정된 OffsetDateTime 반환
     * @param offsetDateTime OffsetDateTime
     * @return LocalDate의 Date가 달의 마지막일, LocalTime이 23시 59분 59초 000000000나노로 설정된 OffsetDateTime
     */
    public static OffsetDateTime endOfMonthNoNano(OffsetDateTime offsetDateTime) {
        var localDate = offsetDateTime.toLocalDate();
        localDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        var result = OffsetDateTime.of(localDate, LOCAL_TIME_END_OF_DAY_NO_NANO, offsetDateTime.getOffset());
        return result;
    }


    //// (e) Start, End

}
