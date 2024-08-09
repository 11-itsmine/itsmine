package com.sparta.itsmine.domain.like;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sparta.itsmine.domain.auction.entity.Auction;
import com.sparta.itsmine.domain.auction.repository.AuctionRepository;
import com.sparta.itsmine.domain.auction.service.AuctionService;
import com.sparta.itsmine.domain.category.entity.Category;
import com.sparta.itsmine.domain.category.repository.CategoryRepository;
import com.sparta.itsmine.domain.like.entity.Like;
import com.sparta.itsmine.domain.like.repository.LikeRepository;
import com.sparta.itsmine.domain.like.service.LikeService;
import com.sparta.itsmine.domain.product.entity.Product;
import com.sparta.itsmine.domain.product.repository.ProductRepository;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.domain.user.repository.UserRepository;
import com.sparta.itsmine.domain.user.utils.UserRole;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LikeTest {
	private static final int THREAD_COUNT = 100;

	@Autowired
	AuctionService auctionService;

	@Autowired
	RedissonClient redissonClient;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	LikeService likeService;

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	AuctionRepository auctionRepository;

	private User seller;
	private User buyer;
	private Product product;
	private Category category;
	@Autowired
	private LikeRepository likeRepository;

	@BeforeAll
	void setup() {
		category = Category.builder()
			.categoryName("testCategory")
			.build();
		categoryRepository.save(category);

		seller = User.builder()
			.username("testSellerUsername")
			.encodedPassword(passwordEncoder.encode("testPassword1!"))
			.name("testSellerName")
			.nickname("testSellerNickname")
			.email("testSellerMail@email.com")
			.role(UserRole.USER)
			.address("testAddress")
			.build();
		userRepository.save(seller);

		buyer = User.builder()
			.username("testBuyerUsername")
			.encodedPassword(passwordEncoder.encode("testPassword1!"))
			.name("testBuyerName")
			.nickname("testBuyerNickname")
			.email("testBuyerMail@email.com")
			.role(UserRole.USER)
			.address("testAddress")
			.build();
		userRepository.save(buyer);

		product = Product.builder()
			.productName("testProduct")
			.description("testDescription")
			.startPrice(1)
			.auctionNowPrice(10000)
			.dueDate(LocalDateTime.now().plusDays(1))
			.category(category)
			.build();
		product.assignUser(seller);
		productRepository.save(product);
	}

	@AfterAll
	void drop() {
		List<Like> like = likeRepository.findAllByProductIdAndUserId(product.getId(), buyer.getId());
		if (like != null) {
			likeRepository.deleteAll(like);
		}
		productRepository.delete(product);
		categoryRepository.delete(category);
		userRepository.delete(seller);
		userRepository.delete(buyer);
	}

	@Test
	@DisplayName("좋아요 분산락 테스트")
	public void test1() {
		long startTime = System.currentTimeMillis();
		IntStream.range(0, THREAD_COUNT).parallel().forEach(i -> {
			System.out.println("시도 횟수 : " + i);
			likeService.createLike(product.getId(), buyer);
			System.out.println("현재 좋아요 수 : " + productRepository.findById(product.getId()).get().getLikeCount());
		});
		long endTime = System.currentTimeMillis();
		Product updatedProduct = productRepository.findById(product.getId()).get();
		System.out.println("테스트 실행 시간: " + (endTime - startTime) + "ms");
		assertEquals(updatedProduct.getLikeCount(), THREAD_COUNT % 2);
	}
}
