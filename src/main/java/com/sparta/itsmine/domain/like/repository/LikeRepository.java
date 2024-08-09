package com.sparta.itsmine.domain.like.repository;

import com.sparta.itsmine.domain.like.dto.LikeResponseDto;
import com.sparta.itsmine.domain.like.entity.Like;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long>, CustomLikeRepository {

    Optional<Like> findByProductIdAndUserId(Long productId, Long UserId);

    void deleteByProductIdAndUserId(Long productId, Long userId);

    boolean existsByProductIdAndUserId(Long productId, Long id);

    List<Like> findAllByProductIdAndUserId(Long id, Long id1);
}