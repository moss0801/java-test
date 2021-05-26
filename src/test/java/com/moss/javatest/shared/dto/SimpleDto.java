package com.moss.javatest.shared.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class SimpleDto {
    int a;
    String b;
    long epochSecond;
}
