package com.sparta.itsmine.domain.report.repository;

import com.sparta.itsmine.domain.report.entity.Report;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByUserId(Long userId);

}
