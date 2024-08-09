import React, {useEffect, useState} from 'react';
import {useNavigate} from 'react-router-dom';
import styled, {css} from 'styled-components';

const Nav = ({userRole}) => {
  const [isLogin, setIsLogin] = useState(false);
  const creamToken = localStorage.getItem('Authorization');
  const navigate = useNavigate();

  useEffect(() => {
    if (creamToken) {
      setIsLogin(true);
    }
  }, [creamToken]);

  const logout = () => {
    localStorage.removeItem('Authorization');
    alert('로그아웃 되었습니다');
    setIsLogin(false);
  };

  const goToMain = () => {
    navigate(`/itsmine`);
  };

  console.log('User role in Nav:', userRole); // 사용자 역할 로그 출력

  return (
      <Container>
        <Logo onClick={goToMain}>ITSMINE</Logo>
        <ButtonContainer>
          <Button login onClick={() => navigate('/products')}>SELL</Button>
          <Button>ABOUT</Button>
          {isLogin ? (
              <>
                <Button login
                        onClick={() => navigate('/profile')}>MYPAGE</Button>
                <Button logout onClick={logout}>LOGOUT</Button>
                {userRole === 'MANAGER' && (
                    <Button login
                            onClick={() => navigate('/admin')}>ADMIN</Button>
                )}
              </>
          ) : (
              <Button login
                      onClick={() => navigate('/itsmine/login')}>LOGIN</Button>
          )}
        </ButtonContainer>
      </Container>
  );
};

const Container = styled.div`
  ${props => props.theme.flex.flexBox('_', 'center', 'space-between')}
  position: fixed;
  top: 0;
  width: 100%;
  height: 4.25rem;
  padding: 0 ${props => props.theme.paddings.xxxl};
  background-color: white;
  z-index: 2000;
`;

const Logo = styled.h1`
  color: black;
  font-size: ${props => props.theme.fontSizes.xxl};
  cursor: pointer;
`;

const ButtonContainer = styled.div``;

const Button = styled.button`
  margin: 0 ${props => props.theme.margins.base};
  border: 0;
  color: black;
  font-size: ${props => props.theme.fontSizes.base};
  background: none;
  cursor: pointer;

  ${({login, theme, logout}) => {
    if (login) {
      return css`
        color: ${theme.colors.green};
        font-weight: ${theme.fontWeights.semiBold};
      `;
    } else if (logout) {
      return `color: ${theme.colors.gray};`;
    }
  }}
`;

export default Nav;
