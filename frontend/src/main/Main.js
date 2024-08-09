import React, {useEffect, useState} from 'react';
import styled from 'styled-components';
import Carousel from "../components/carousel/Carousel";
import ItemList from "../components/item/ItemList";
import Board from "../components/chat/Board";
import axiosInstance from '../api/axiosInstance'; // Axios 인스턴스 가져오기
import Nav from "../components/nav/Nav"; // Nav 컴포넌트 가져오기

function Main() {
  const [items, setItems] = useState([]);
  const [chatRooms, setChatRooms] = useState([]);
  const [userRole, setUserRole] = useState(''); // 사용자 역할 상태 추가

  useEffect(() => {
    // 채팅방 데이터 가져오기
    axiosInstance.get('/v1/chatrooms')
    .then(response => {
      setChatRooms(response.data);
    })
    .catch(error => {
      console.error('Failed to fetch chat rooms', error);
    });

    // 사용자 역할 가져오기
    const fetchUserRole = async () => {
      const token = localStorage.getItem('Authorization');
      if (token) {
        try {
          console.log('Fetching user role with token:', token);
          const response = await axiosInstance.get('/v1/users/role', {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });
          console.log('User role response:', response.data);
          setUserRole(response.data.userRole);
        } catch (error) {
          console.error('Failed to fetch user role:',
              error.response ? error.response.data : error.message);
        }
      }
    };

    fetchUserRole();
  }, []);

  return (
      <div>
        <Nav userRole={userRole}/>
        <MainWrapper>
          <Carousel/>
          <ItemList items={items}/>
          <Board chatRooms={chatRooms}/>
          {/* 사용자 역할에 따라 추가 버튼을 렌더링 */}
          {userRole === 'MANAGER' && (
              <div>
                <button
                    onClick={() => window.location.href = '/reported-users'}>
                  신고된 유저 목록
                </button>
                <button onClick={() => window.location.href = '/banned-users'}>
                  유저 리스트
                </button>
              </div>
          )}
        </MainWrapper>
      </div>
  );
}

const MainWrapper = styled.div`
  margin-top: 5rem;
`;

export default Main;
