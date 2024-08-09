package com.sparta.itsmine.domain.report.dto;

import lombok.Data;

@Data
public class BlockRequestDto {

    private Long userId;
    private Long blockPlusDate;
    private String benReason;
}
