import React, { useEffect, useState } from "react";
import axiosInstance from "../../api/axiosInstance";
import styled from "styled-components";
import { useNavigate, useParams } from "react-router-dom";

const AuctionComponent = () => {
  const [product, setProduct] = useState(null);
  const [bidPrice, setBidPrice] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [isLiked, setIsLiked] = useState(false); // 좋아요 상태 추가

  const navigate = useNavigate();
  const { productId } = useParams();

  // 제품 정보를 가져오는 함수
  const fetchProduct = async () => {
    try {
      const response = await axiosInstance.get(`/products/${productId}`);
      setProduct(response.data.data);
    } catch (err) {
      setError("제품 정보를 가져오는데 실패했습니다.");
      console.error("Error fetching product data:", err);
    }
  };

  // 좋아요 상태를 가져오는 함수
  const fetchLikeStatus = async () => {
    try {
      const token = localStorage.getItem("Authorization");
      const response = await axiosInstance.get(
          `/users/products/${productId}/likes`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
      );
      setIsLiked(response.data.isLiked);
    } catch (err) {
      console.error("Error fetching like status:", err);
    }
  };

  // 좋아요 상태를 토글하는 함수
  const toggleLike = async () => {
    try {
      const token = localStorage.getItem("Authorization");
      await axiosInstance.post(
          `/users/products/${productId}/likes`,
          {},
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
      );
      setIsLiked(!isLiked); // 상태를 토글
    } catch (err) {
      setError("좋아요 변경에 실패했습니다. 다시 시도하세요.");
      console.error("Error toggling like status:", err);
    }
  };

  // 컴포넌트가 마운트될 때 제품 정보와 좋아요 상태를 가져옴
  useEffect(() => {
    fetchProduct();
    fetchLikeStatus();
  }, [productId]);

  // 입찰을 처리하는 함수
  const handleBid = async () => {
    if (!bidPrice) {
      setError("입찰 가격을 입력하세요.");
      return;
    }

    const token = localStorage.getItem("Authorization");

    try {
      const response = await axiosInstance.post(
          `/products/${productId}/auctions`,
          { bidPrice },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
      );

      setMessage("입찰이 성공적으로 완료되었습니다.");
      setError("");
      navigate("/itsmine");
    } catch (err) {
      setError("입찰에 실패했습니다. 다시 시도하세요.");
      setMessage("");
      console.error("Error creating auction:", err);
    }
  };

  // 다음 이미지로 이동
  const nextImage = () => {
    setCurrentImageIndex((prevIndex) =>
        prevIndex === product.imagesUrl.length - 1 ? 0 : prevIndex + 1
    );
  };

  // 이전 이미지로 이동
  const prevImage = () => {
    setCurrentImageIndex((prevIndex) =>
        prevIndex === 0 ? product.imagesUrl.length - 1 : prevIndex - 1
    );
  };

  // 즉시 구매 가격을 입찰 가격으로 설정
  const handleBuyNow = () => {
    setBidPrice(product.auctionNowPrice);
  };

  // 현재 입찰가를 입찰 가격에 자동 입력
  const handleCurrentPrice = () => {
    setBidPrice(product.currentPrice);
  };

  // 로딩 중 또는 에러 메시지 처리
  if (error) {
    return <ErrorText>{error}</ErrorText>;
  }

  if (!product) {
    return <LoadingText>Loading...</LoadingText>;
  }

  return (
      <Container>
        <ImageSlider>
          <Arrow left onClick={prevImage}>
            &lt;
          </Arrow>
          <ProductImage
              src={product.imagesUrl[currentImageIndex]}
              alt={`Product ${currentImageIndex}`}
          />
          <Arrow onClick={nextImage}>&gt;</Arrow>
        </ImageSlider>
        <Indicator>
          {product.imagesUrl.map((_, index) => (
              <Dot key={index} isActive={index === currentImageIndex} />
          ))}
        </Indicator>
        <Details>
          <Title>{product.productName}</Title>
          <LikeButton onClick={toggleLike} isLiked={isLiked}>
            {isLiked ? "♥" : "♡"}
          </LikeButton>
          <Description>{product.description}</Description>
        </Details>
        <AdditionalInfo>
          <InfoText>경매 시작가: {product.startPrice}원</InfoText>
        </AdditionalInfo>
        <ButtonContainer>
          <PriceButton className="buy-btn" onClick={handleBuyNow}>
            구매 {product.auctionNowPrice.toLocaleString()}원 즉시 구매가
          </PriceButton>
          <PriceButton className="bid-btn" onClick={handleCurrentPrice}>
            입찰 {product.currentPrice.toLocaleString()}원 현재 입찰가
          </PriceButton>
        </ButtonContainer>
        <BidSection>
          <h2>입찰하기</h2>
          <BidInput
              type="number"
              value={bidPrice}
              onChange={(e) => setBidPrice(e.target.value)}
              placeholder="입찰 가격"
              min={product.currentPrice + 1}
          />
          <BidButton onClick={handleBid}>입찰</BidButton>
          {message && <SuccessText>{message}</SuccessText>}
          {error && <ErrorText>{error}</ErrorText>}
        </BidSection>
      </Container>
  );
};

export default AuctionComponent;

// 스타일 컴포넌트 정의
const Container = styled.div`
  width: 100%;
  max-width: 600px;
  margin: 100px auto 0; // 위쪽에 100px의 여백 추가
  background-color: #f2f2f2;
  padding: 20px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
`;

const ImageSlider = styled.div`
  width: 100%;
  height: 300px;
  background-color: #8b5d5d;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  margin-bottom: 10px;
`;

const ProductImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const Arrow = styled.div`
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: transparent; /* 배경을 투명하게 설정 */
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1.5rem;
  user-select: none;
  color: #fff; /* 화살표 색상을 흰색으로 설정 */
  &:hover {
    background-color: rgba(255, 255, 255, 0.2); /* 호버 시 약간의 배경색 추가 */
  }

  ${({ left }) => (left ? `left: 10px;` : `right: 10px;`)}
`;

const Indicator = styled.div`
  display: flex;
  justify-content: center;
  margin-top: 10px;
`;

const Dot = styled.div`
  width: 8px;
  height: 8px;
  background-color: ${({ isActive }) => (isActive ? "#000" : "#bbb")};
  border-radius: 50%;
  margin: 0 5px;
  transition: background-color 0.3s ease;
`;

const Details = styled.div`
  width: 100%;
  text-align: center;
  padding: 10px;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  position: relative; /* 좋아요 버튼 위치 설정을 위해 추가 */
`;

const Title = styled.div`
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 5px;
`;

const Description = styled.div`
  color: #666;
  margin-top: 10px; /* 좋아요 버튼과 설명 사이의 간격 */
`;

const AdditionalInfo = styled.div`
  width: 100%;
  text-align: center;
  padding: 10px;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 20px;
`;

const InfoText = styled.div`
  color: #999;
`;

const ButtonContainer = styled.div`
  display: flex;
  justify-content: space-between;
  width: 100%;
  margin-bottom: 20px;
`;

const PriceButton = styled.button`
  padding: 15px;
  border: none;
  border-radius: 5px;
  color: white;
  cursor: pointer;
  font-size: 16px;
  width: 48%;
  font-weight: bold;

  &.buy-btn {
    background-color: #e74c3c;
  }

  &.bid-btn {
    background-color: #27ae60;
  }
`;

const BidSection = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
`;

const BidInput = styled.input`
  width: 100%;
  max-width: 200px;
  margin-bottom: 10px;
  padding: 10px;
  font-size: 1rem;
  border: 1px solid #ccc;
  border-radius: 5px;
`;

const BidButton = styled.button`
  padding: 10px 20px;
  font-size: 1rem;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  &:hover {
    background-color: #45a049;
  }
`;

const SuccessText = styled.div`
  color: green;
  font-size: 1rem;
  margin-top: 10px;
`;

const ErrorText = styled.div`
  color: red;
  font-size: 1rem;
  margin-top: 10px;
`;

const LoadingText = styled.div`
  font-size: 1.2rem;
  margin-top: 50px;
`;

const LikeButton = styled.button`
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 10px;
  font-size: 1.5rem;
  background-color: transparent;
  color: ${({ isLiked }) => (isLiked ? "#e74c3c" : "#bbb")};
  border: none;
  cursor: pointer;
  &:hover {
    color: ${({ isLiked }) => (isLiked ? "#c0392b" : "#888")};
  }
`;
