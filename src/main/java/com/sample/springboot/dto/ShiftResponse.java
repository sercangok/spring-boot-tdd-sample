package com.sample.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftResponse {
    private UUID id;
    private UUID talentId;
    private UUID jobId;
    private Instant start;
    private Instant end;
}
