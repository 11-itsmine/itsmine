package com.sparta.itsmine.domain.qna.repository;

import com.sparta.itsmine.domain.qna.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Qna, Long> {

}
