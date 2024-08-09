// package com.sparta.itsmine.domain.auction;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.stream.IntStream;
//
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.redisson.api.RedissonClient;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.junit.jupiter.SpringExtension;
//
// import com.sparta.itsmine.domain.auction.dto.AuctionRequestDto;
// import com.sparta.itsmine.domain.auction.dto.AuctionResponseDto;
// import com.sparta.itsmine.domain.auction.entity.Auction;
// import com.sparta.itsmine.domain.auction.repository.AuctionRepository;
// import com.sparta.itsmine.domain.auction.service.AuctionService;
// import com.sparta.itsmine.domain.category.entity.Category;
// import com.sparta.itsmine.domain.category.repository.CategoryRepository;
// import com.sparta.itsmine.domain.product.entity.Product;
// import com.sparta.itsmine.domain.product.repository.ProductRepository;
// import com.sparta.itsmine.domain.user.entity.User;
// import com.sparta.itsmine.domain.user.repository.UserRepository;
// import com.sparta.itsmine.domain.user.utils.UserRole;
//
// @ExtendWith(SpringExtension.class)
// @SpringBootTest
// @ActiveProfiles("test")
// public class AuctionLockTest {
// 	/**
// 	 웬만하면 실행 안하는게 좋습니다.. 자꾸 ActiveMQ 폭발하는듯 합니다
// 	 **/
// 	private static final String LOCK_KEY = "testLock";
// 	private static final int THREAD_COUNT = 100;
//
// 	@Autowired
// 	AuctionService auctionService;
//
// 	@Autowired
// 	RedissonClient redissonClient;
//
// 	@Autowired
// 	PasswordEncoder passwordEncoder;
//
// 	private static User seller;
// 	private static User buyer;
// 	private static Product product;
// 	private static Category category;
// 	@Autowired
// 	private UserRepository userRepository;
// 	@Autowired
// 	private ProductRepository productRepository;
// 	@Autowired
// 	private CategoryRepository categoryRepository;
// 	@Autowired
// 	private AuctionRepository auctionRepository;
//
// 	@BeforeAll
// 	static void setup(@Autowired CategoryRepository categoryRepository,
// 		@Autowired UserRepository userRepository,
// 		@Autowired ProductRepository productRepository,
// 		@Autowired PasswordEncoder passwordEncoder) {
// 		category = Category.builder()
// 			.categoryName("testCategory")
// 			.build();
// 		categoryRepository.save(category);
//
// 		seller = User.builder()
// 			.username("testSellerUsername")
// 			.encodedPassword(passwordEncoder.encode("testPassword1!"))
// 			.name("testSellerName")
// 			.nickname("testSellerNickname")
// 			.email("testSellerMail@email.com")
// 			.role(UserRole.USER)
// 			.address("testAddress")
// 			.build();
// 		userRepository.save(seller);
//
// 		buyer = User.builder()
// 			.username("testBuyerUsername")
// 			.encodedPassword(passwordEncoder.encode("testPassword1!"))
// 			.name("testBuyerName")
// 			.nickname("testBuyerNickname")
// 			.email("testBuyerMail@email.com")
// 			.role(UserRole.USER)
// 			.address("testAddress")
// 			.build();
// 		userRepository.save(buyer);
//
// 		product = Product.builder()
// 			.productName("testProduct")
// 			.description("testDescription")
// 			.startPrice(1)
// 			.auctionNowPrice(10000)
// 			.dueDate(LocalDateTime.now().plusDays(1))
// 			.category(category)
// 			.build();
// 		product.assignUser(seller);
// 		productRepository.save(product);
// 	}
//
// 	@AfterAll
// 	static void drop(@Autowired AuctionRepository auctionRepository,
// 		@Autowired ProductRepository productRepository,
// 		@Autowired CategoryRepository categoryRepository,
// 		@Autowired UserRepository userRepository) {
// 		List<Auction> auctions = auctionRepository.findAllByProductId(product.getId());
// 		auctionRepository.deleteAll(auctions);
// 		productRepository.delete(product);
// 		categoryRepository.delete(category);
// 		userRepository.delete(seller);
// 		userRepository.delete(buyer);
// 	}
//
// 	// 테스트 시 입찰가격 exception 두 군데를 주석처리해야함.
// 	@Test
// 	@DisplayName("입찰 분산락 테스트")
// 	public void test1() {
// 		IntStream.range(0, THREAD_COUNT).parallel().forEach(i -> {
// 				AuctionRequestDto requestDto = new AuctionRequestDto();
// 				requestDto.setBidPrice(i * 10);
// 				System.out.println("입찰시도 " + i);
// 				AuctionResponseDto response = auctionService.createAuction(buyer, product.getId(), requestDto);
// 				System.out.println("현재가격 : " + response.getBidPrice());
// 				System.out.println("입찰 카운트 : " + auctionRepository.countByProductId(product.getId()));
// 		});
//
// 		assertEquals(auctionRepository.countByProductId(product.getId()), THREAD_COUNT);
// 	}
// }
