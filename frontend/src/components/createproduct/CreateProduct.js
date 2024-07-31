import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Box, Button, Container, Grid, TextField, Typography, IconButton, MenuItem, Select, InputLabel, FormControl } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import DeleteIcon from '@mui/icons-material/Delete';

const ProductCreatePage = () => {
  const [productName, setProductName] = useState('');
  const [description, setDescription] = useState('');
  const [auctionNowPrice, setAuctionNowPrice] = useState('');
  const [startPrice, setStartPrice] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [categoryName, setCategoryName] = useState('');
  const [categories, setCategories] = useState([]);
  const [imageUrls, setImageUrls] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await axios.get('http://localhost:8080/categories');
        setCategories(response.data.data);
      } catch (error) {
        console.error('Error fetching categories:', error);
      }
    };

    fetchCategories();
  }, []);

  const handleImageChange = async (event) => {
    const files = event.target.files;
    if (!files.length) return;

    const formData = new FormData();
    Array.from(files).forEach((file) => {
      formData.append('file', file);
    });

    try {
      const response = await axios.post('http://localhost:8080/s3/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      const newImageUrls = [...imageUrls, ...response.data.imagesUrl];
      setImageUrls(newImageUrls.slice(0, 5)); // 최대 5개 이미지 저장
    } catch (error) {
      console.error('Error uploading image:', error);
    }
  };

  const handleImageDelete = (index) => {
    const newImageUrls = [...imageUrls];
    newImageUrls.splice(index, 1);
    setImageUrls(newImageUrls);
  };

  const token = localStorage.getItem('Authorization');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const productData = {
        productCreateDto: {
          productName,
          description,
          auctionNowPrice,
          startPrice,
          dueDate,
          categoryName
        },
        productImagesRequestDto: {
          imagesUrl: imageUrls
        }
      };

      await axios.post('http://localhost:8080/products', productData, {
        headers: {
          Authorization: token
        }
      });

      navigate('/'); // 상품 등록 후 홈 페이지로 이동
    } catch (error) {
      console.error('Error creating product:', error);
    }
  };

  return (
      <Container sx={{ backgroundColor: '#f2f2f2', padding: 3, borderRadius: 2 }}>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            상품 등록
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Button variant="contained" component="label">
                이미지 업로드
                <input type="file" hidden onChange={handleImageChange} multiple />
              </Button>
            </Grid>
            <Grid item xs={12}>
              <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 2, mt: 2 }}>
                {imageUrls.map((url, index) => (
                    <Box key={index} sx={{ position: 'relative', width: '18%', height: 'auto' }}>
                      <img src={url} alt={`이미지 ${index + 1}`} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      <IconButton
                          onClick={() => handleImageDelete(index)}
                          sx={{ position: 'absolute', top: 0, right: 0 }}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </Box>
                ))}
              </Box>
            </Grid>
            <Grid item xs={12}>
              <TextField
                  fullWidth
                  label="상품명"
                  value={productName}
                  onChange={(e) => setProductName(e.target.value)}
                  required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                  fullWidth
                  label="설명"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  multiline
                  rows={4}
                  required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                  fullWidth
                  label="즉시 구매가"
                  type="number"
                  value={auctionNowPrice}
                  onChange={(e) => setAuctionNowPrice(e.target.value)}
                  required
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                  fullWidth
                  label="시작 가격"
                  type="number"
                  value={startPrice}
                  onChange={(e) => setStartPrice(e.target.value)}
                  required
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                  fullWidth
                  label="경매 진행 시간 (시간 단위)"
                  type="number"
                  value={dueDate}
                  onChange={(e) => setDueDate(e.target.value)}
                  required
              />
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth required>
                <InputLabel id="category-label">카테고리</InputLabel>
                <Select
                    labelId="category-label"
                    value={categoryName}
                    onChange={(e) => setCategoryName(e.target.value)}
                    label="카테고리"
                >
                  {categories.map((category, index) => (
                      <MenuItem key={index} value={category.categoryName}>
                        {category.categoryName}
                      </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <Button type="submit" variant="contained" color="primary" fullWidth>
                등록
              </Button>
            </Grid>
          </Grid>
        </Box>
      </Container>
  );
};

export default ProductCreatePage;