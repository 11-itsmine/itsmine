import React, {useEffect, useState} from 'react';
import './App.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import SignUp from "./components/auth/Signup";
import Main from "./main/Main";
import Footer from "./components/footer/Footer";
import Nav from "./components/nav/Nav";
import CreateProduct from "./components/createproduct/CreateProduct";
import {ThemeProvider} from "styled-components";
import SignIn from "./components/auth/Signin";
import theme from "./styles/theme";
import ItemList from "./components/item/ItemList";
import AuctionComponent from "./components/Auction/AuctionComponent";
import Profile from "./components/profile/profile";
import Item from "./components/item/Item";
import KakaoCallback from "./api/KakaoCallback";
import AdminPage from "./components/backOffice/AdminPage"; // 관리자 페이지 컴포넌트 추가
import axiosInstance from './api/axiosInstance'; // axiosInstance 가져오기

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('Authorization');
    if (token) {
      setIsLoggedIn(true);
      fetchUserRole(token); // 사용자 역할을 가져오는 함수 호출
    }
  }, []);

  const fetchUserRole = async (token) => {
    try {
      console.log('Fetching user role with token:', token);
      const response = await axiosInstance.get('/v1/users/role', {
        headers: {
          'Authorization': token.trim()
        }
      });
      console.log('User role response:', response.data);
      setUserRole(response.data.userRole);
    } catch (error) {
      console.error('Failed to fetch user role:', error);
    }
  };

  const handleLogin = () => {
    setIsLoggedIn(true);
    const token = localStorage.getItem('Authorization');
    if (token) {
      fetchUserRole(token); // 로그인 후 사용자 역할을 다시 가져옴
    }
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    localStorage.removeItem('Authorization');
    setUserRole(''); // 로그아웃 시 사용자 역할 초기화
  };

  return (
      <ThemeProvider theme={theme}>
        <BrowserRouter>
          <Nav userRole={userRole}/>
          <Routes>
            <Route path="/itsmine" element={<Main/>}/>
            <Route path="/itsmine/login"
                   element={<SignIn isLoggedIn={isLoggedIn}
                                    onLogin={handleLogin}/>}/>
            <Route path="/signup" element={<SignUp/>}/>
            <Route path="/oauth/callback/kakao"
                   element={<KakaoCallback isLoggedIn={isLoggedIn}
                                           onLogin={handleLogin}/>}/>
            <Route path="/products" element={<CreateProduct/>}/>
            <Route path="/items" element={<ItemList/>}/>
            <Route path="/item" element={<Item/>}/>
            <Route path="/products/:productId"
                   element={<AuctionComponent/>}/>
            <Route path="/profile" element={<Profile/>}/>
            {userRole === 'MANAGER' && (
                <Route path="/admin" element={<AdminPage/>}/> // 관리자 페이지 접근 제어
            )}
          </Routes>
          <Footer/>
        </BrowserRouter>
      </ThemeProvider>
  );
}

export default App;
