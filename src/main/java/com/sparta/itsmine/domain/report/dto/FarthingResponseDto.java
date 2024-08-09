package com.sparta.itsmine.domain.report.dto;

import com.sparta.itsmine.domain.report.entity.ReportType;
import lombok.Data;

@Data
public class FarthingResponseDto {

    private Long reportId;
    private ReportType reportType;
    private Long reportTypeId;

}
