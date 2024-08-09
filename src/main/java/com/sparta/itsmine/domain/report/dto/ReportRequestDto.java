package com.sparta.itsmine.domain.report.dto;

import com.sparta.itsmine.domain.report.entity.ReportType;
import lombok.Data;

@Data
public class ReportRequestDto {

    private String title;
    private String content;
    private ReportType reportType;
}
