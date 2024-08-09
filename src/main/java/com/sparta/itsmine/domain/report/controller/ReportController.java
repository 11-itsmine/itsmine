package com.sparta.itsmine.domain.report.controller;

import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.REPORT_SUCCESS_CREATE;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.REPORT_SUCCESS_UPDATE;

import com.sparta.itsmine.domain.report.dto.BlockRequestDto;
import com.sparta.itsmine.domain.report.dto.ReportRequestDto;
import com.sparta.itsmine.domain.report.dto.ReportResponseDto;
import com.sparta.itsmine.domain.report.service.ReportService;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.global.common.response.HttpResponseDto;
import com.sparta.itsmine.global.common.response.ResponseUtils;
import com.sparta.itsmine.global.security.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<HttpResponseDto> creteReport(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReportRequestDto requestDto) {
        User user = userDetails.getUser();
        reportService.createReport(user, requestDto);
        return ResponseUtils.of(REPORT_SUCCESS_CREATE);
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<HttpResponseDto> updateReport(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ReportRequestDto requestDto,
            @PathVariable Long reportId
    ) {
        User user = userDetails.getUser();
        reportService.updateReport(user, requestDto, reportId);
        return ResponseUtils.of(REPORT_SUCCESS_UPDATE);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<HttpResponseDto> deleteReport(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long reportId
    ) {
        User user = userDetails.getUser();
        reportService.deleteReport(user, reportId);
        return ResponseUtils.of(REPORT_SUCCESS_UPDATE);
    }

    @GetMapping
    public ResponseEntity<HttpResponseDto> getAllReport(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        List<ReportResponseDto> responseDtos = reportService.getAllReport(user);
        return ResponseUtils.of(REPORT_SUCCESS_CREATE, responseDtos);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<HttpResponseDto> getReport(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long reportId) {
        User user = userDetails.getUser();
        ReportResponseDto responseDto = reportService.getReportDetail(user, reportId);
        return ResponseUtils.of(REPORT_SUCCESS_CREATE, responseDto);
    }

    @PutMapping("/block")
    public ResponseEntity<HttpResponseDto> blockUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody BlockRequestDto requestDto
    ) {
        User user = userDetails.getUser();
        reportService.blockUser(user, requestDto);
        return ResponseUtils.of(REPORT_SUCCESS_CREATE);
    }

//    @GetMapping("/farthing/{reportType}")
//    public ResponseEntity<HttpResponseDto> reportFarthing(
//            @AuthenticationPrincipal UserDetailsImpl userDetails,
//            @PathVariable String reportType) {
//        User user = userDetails.getUser();
//        reportService.reportFarthing(user, reportType);
//    }
}
