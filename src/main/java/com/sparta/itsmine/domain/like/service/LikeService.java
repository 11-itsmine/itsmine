package com.sparta.itsmine.domain.like.service;

import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.SUCCESS_TO_LIKE;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.SUCCESS_TO_REMOVE_LIKE;

import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.itsmine.domain.like.dto.LikeResponseDto;
import com.sparta.itsmine.domain.like.entity.Like;
import com.sparta.itsmine.domain.like.repository.LikeAdapter;
import com.sparta.itsmine.domain.like.repository.LikeRepository;
import com.sparta.itsmine.domain.product.entity.Product;
import com.sparta.itsmine.domain.product.repository.ProductAdapter;
import com.sparta.itsmine.domain.product.repository.ProductRepository;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.global.common.response.ResponseCodeEnum;
import com.sparta.itsmine.global.lock.DistributedLock;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

	private final LikeRepository likeRepository;
	private final ProductAdapter productAdapter;
	private final LikeAdapter likeAdapter;
	private final RedissonClient redissonClient;
	private static final String LOCK_KEY_PREFIX = "likeLock";
	private final ProductRepository productRepository;

	@DistributedLock(prefix = LOCK_KEY_PREFIX, key = "productId")
	public ResponseCodeEnum createLike(Long productId, User user) {
		Product product = productAdapter.getProduct(productId);
		return likeRepository.findByProductIdAndUserId(productId, user.getId())
			.map(like -> {
				// 존재하는 경우 좋아요 수 감소
				likeCounter(product, false);
				likeRepository.delete(like);
				return SUCCESS_TO_REMOVE_LIKE;
			})
			.orElseGet(() -> {
					// 없는 경우 좋아요 수 증가
					likeRepository.save(new Like(product, user));
					likeCounter(product, true);
					return SUCCESS_TO_LIKE;
				}
			);

	}

	public LikeResponseDto getLike(Long productId, User user) {
		Like likeProduct = likeAdapter.getLike(productId, user.getId());
		boolean isLike = likeAdapter.isUserLikedProduct(productId, user);
		return new LikeResponseDto(likeProduct, isLike);
	}

	@Transactional
	protected void likeCounter(Product product, boolean countUp) {

		if (countUp) {
			product.countUpdate(1);
		} else {
			product.countUpdate(-1);
		}
		productRepository.save(product);
	}
}


