package com.sparta.itsmine.domain.report.entity;

import com.sparta.itsmine.domain.report.dto.ReportRequestDto;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.global.common.TimeStamp;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends TimeStamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_user_id")
    private User user;

    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private Long reportTypeId;

    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    public Report(User user, ReportRequestDto requestDto) {
        this.user = user;
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.reportType = requestDto.getReportType();
        this.reportStatus = ReportStatus.PROGRESS;
    }

    public void update(ReportRequestDto requestDto) {
        this.content = requestDto.getContent();
        this.reportType = requestDto.getReportType();
    }
}
