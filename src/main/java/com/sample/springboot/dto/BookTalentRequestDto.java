package com.sample.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class BookTalentRequestDto {
    private UUID talent;
}
