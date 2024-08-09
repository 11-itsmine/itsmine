import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';

const KakaoCallback = ({ onLogin }) => {
  const navigate = useNavigate();

  useEffect(() => {
    const url = new URL(window.location.href);
    const code = url.searchParams.get('code');
    if (code) {
      const handleKakaoLogin = async (code) => {
        try {
          const response = await axiosInstance.get(`/v1/users/oauth/kakao?code=${code}`);
          const token = response.data.data;  // Get token from headers
          if (token) {
            localStorage.setItem('Authorization', token);
            onLogin();
            navigate('/itsmine', { replace: true });
          }
        } catch (error) {
          console.error('Kakao Login failed:', error);
        }
      };

      handleKakaoLogin(code);
    }
  }, [navigate, onLogin]);

  return (
      <div>
        <h2>카카오 로그인 처리 중...</h2>
      </div>
  );
};

export default KakaoCallback;
