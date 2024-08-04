import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import styled from 'styled-components';
import axiosInstance from "../../api/axiosInstance";

const Item = ({
  productName,
  description,
  currentPrice,
  auctionNowPrice,
  dueDate,
  productId
}) => {
  const [product, setProduct] = useState(null);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

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

  useEffect(() => {
    fetchProduct();
  }, [productId]);

  const goToDetail = () => {
    navigate(`/products/${productId}`);
  };

  const nextImage = (e) => {
    e.stopPropagation();
    if (product && product.imagesUrl) {
      setCurrentImageIndex(
          (prevIndex) => (prevIndex + 1) % product.imagesUrl.length);
    }
  };

  const prevImage = (e) => {
    e.stopPropagation();
    if (product && product.imagesUrl) {
      setCurrentImageIndex((prevIndex) =>
          prevIndex === 0 ? product.imagesUrl.length - 1 : prevIndex - 1
      );
    }
  };

  if (error) {
    return <div>{error}</div>;
  }

  return (
      <ItemComponent onClick={goToDetail}>
        <ImageContainer>
          {product && product.imagesUrl && product.imagesUrl.length > 0 ? (
              <>
                <Arrow left onClick={prevImage}>&lt;</Arrow>
                <ItemImg
                    src={product.imagesUrl[currentImageIndex]}
                    alt={`${productName} image`}
                />
                <Arrow onClick={nextImage}>&gt;</Arrow>
              </>
          ) : (
              <ItemImg
                  src="/path/to/placeholder-image.jpg"
                  alt="placeholder"
              />
          )}
        </ImageContainer>
        <ItemDetails>
          <ItemName>{productName}</ItemName>
          <ItemDescription>{description}</ItemDescription>
          <ItemPrice>{currentPrice ? `${currentPrice.toLocaleString()}원`
              : '가격 미정'}</ItemPrice>
          <AuctionDetails>
            <DetailText>즉시 구매가: {auctionNowPrice
                ? `${auctionNowPrice.toLocaleString()}원` : '가격 미정'}</DetailText>
            <DetailText>마감 시간: {new Date(dueDate).toLocaleString()}</DetailText>
          </AuctionDetails>
        </ItemDetails>
      </ItemComponent>
  );
};

export default Item;

const ItemComponent = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 2rem;
  cursor: pointer;
  transition: transform 0.2s;
  width: 14rem;

  &:hover {
    transform: translateY(-5px);
  }
`;

const ImageContainer = styled.div`
  position: relative;
  width: 14rem;
  height: 14rem;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const ItemImg = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
  background-color: #f0f0f0;
`;

const Arrow = styled.div`
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  border-radius: 50%;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 1.5rem;
  color: #000;
  user-select: none;

  ${({left}) => (left ? `left: 10px;` : `right: 10px;`)}
`;

const ItemDetails = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 1rem;
  text-align: center;
`;

const ItemName = styled.div`
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
`;

const ItemDescription = styled.div`
  font-size: 1rem;
  color: gray;
  margin-bottom: 0.5rem;
`;

const ItemPrice = styled.div`
  font-size: 1.2rem;
  font-weight: bold;
  color: #ff4500;
`;

const AuctionDetails = styled.div`
  margin-top: 0.5rem;
  font-size: 0.9rem;
  color: #555;
`;

const DetailText = styled.div`
  margin: 0.2rem 0;
`;
