package com.moss.javatest.shared.dto;

import lombok.Data;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
public class SimpleModel {
    int a;
    String b;
    OffsetDateTime time;
}
