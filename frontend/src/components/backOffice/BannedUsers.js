import React, {useEffect, useState} from 'react';
import axiosInstance from '../../api/axiosInstance'; // API 호출을 위한 axios 인스턴스 가져오기
import styled from 'styled-components';
import Modal from './Modal'; // 모달 컴포넌트 가져오기

const Users = () => {
  const [users, setUsers] = useState([]); // 유저 목록을 저장할 상태
  const [error, setError] = useState(''); // 오류 메시지를 저장할 상태
  const [selectedUser, setSelectedUser] = useState(null); // 선택된 유저의 프로필 정보를 저장할 상태
  const [isModalOpen, setIsModalOpen] = useState(false); // 모달 창 열기 상태
  const [banDays, setBanDays] = useState(0); // 벤 기간 상태
  const [banReason, setBanReason] = useState(''); // 벤 사유 상태

  useEffect(() => {
    axiosInstance.get('/v1/users/list') // 유저 목록을 가져오는 API 호출
    .then(response => {
      setUsers(response.data.data); // 성공적으로 가져오면 유저 목록 상태에 저장
    })
    .catch(error => {
      setError('유저 목록을 불러오는 중 오류가 발생했습니다.'); // 오류가 발생하면 오류 메시지 설정
    });
  }, []);

  const handleUserClick = (user) => {
    console.log("Selected User:", user);  // 로그를 통해 선택된 유저의 정보를 확인
    setSelectedUser(user); // 유저를 클릭했을 때 선택된 유저의 정보를 상태에 저장
  };

  const handleBackToList = () => {
    setSelectedUser(null); // 프로필 보기에서 목록으로 돌아갈 때 상태 초기화
  };

  const handleBanClick = () => {
    setIsModalOpen(true); // 벤하기 버튼 클릭 시 모달 창 열기
  };

  const handleModalClose = () => {
    setIsModalOpen(false); // 모달 창 닫기
    setBanDays(0); // 벤 기간 초기화
    setBanReason(''); // 벤 사유 초기화
  };

  const handleBanSubmit = () => {
    if (!selectedUser || !selectedUser.userId) {
      alert('유저 ID가 올바르지 않습니다.');
      return;
    }

    const banData = {
      userId: selectedUser.userId,  // userId를 사용
      blockPlusDate: banDays, // 벤 기간 (일수)
      benReason: banReason, // 벤 사유
    };

    axiosInstance.put('/v1/report/block', banData)
    .then(response => {
      alert('사용자가 성공적으로 벤 처리되었습니다.');
      handleModalClose();
      handleBackToList(); // 목록으로 돌아가기
    })
    .catch(error => {
      alert('벤 처리에 실패했습니다. 다시 시도해주세요.');
    });
  };

  return (
      <Container>
        {selectedUser ? (
            <ProfileContainer>
              <ProfileHeader>
                <ProfileImage
                    src={selectedUser.imageUrls[0] || 'default_profile.png'}
                    alt="프로필 이미지"
                />
                <ProfileInfo>
                  <ProfileName>{selectedUser.nickname}</ProfileName>
                </ProfileInfo>
              </ProfileHeader>
              <ProfileBody>
                <SectionTitle>유저 정보</SectionTitle>
                <ProfileDetailTable>
                  <tbody>
                  <DetailRow>
                    <DetailTh>이름:</DetailTh>
                    <DetailTd>{selectedUser.name}</DetailTd>
                  </DetailRow>
                  <DetailRow>
                    <DetailTh>닉네임:</DetailTh>
                    <DetailTd>{selectedUser.nickname}</DetailTd>
                  </DetailRow>
                  <DetailRow>
                    <DetailTh>이메일:</DetailTh>
                    <DetailTd>{selectedUser.email}</DetailTd>
                  </DetailRow>
                  <DetailRow>
                    <DetailTh>주소:</DetailTh>
                    <DetailTd>{selectedUser.address}</DetailTd>
                  </DetailRow>
                  </tbody>
                </ProfileDetailTable>
              </ProfileBody>
              <ButtonContainer>
                <BackButton onClick={handleBackToList}>목록으로 돌아가기</BackButton>
                <BanButton onClick={handleBanClick}>벤하기</BanButton>
              </ButtonContainer>
            </ProfileContainer>
        ) : (
            <>
              <h2>유저 목록</h2>
              {error && <ErrorText>{error}</ErrorText>} {/* 오류가 있을 경우 표시 */}
              <Table>
                <thead>
                <tr>
                  <Th>번호</Th>
                  <Th>이름</Th>
                  <Th>닉네임</Th>
                  <Th>이메일</Th>
                  <Th>주소</Th>
                </tr>
                </thead>
                <tbody>
                {users.map((user, index) => (
                    <Tr key={user.userId} onClick={() => handleUserClick(user)}>
                      <Td>{index + 1}</Td> {/* 번호를 순서대로 표시 */}
                      <Td>{user.name}</Td>
                      <Td>{user.nickname}</Td>
                      <Td>{user.email}</Td>
                      <Td>{user.address}</Td>
                    </Tr>
                ))}
                </tbody>
              </Table>
            </>
        )}

        {/* 모달 창 */}
        {isModalOpen && (
            <Modal onClose={handleModalClose}>
              <ModalContent>
                <h3>벤 기간 설정</h3>
                <label>벤 기간 (일수):</label>
                <input
                    type="number"
                    value={banDays}
                    onChange={(e) => setBanDays(e.target.value)}
                />
                <label>벤 사유:</label>
                <textarea
                    value={banReason}
                    onChange={(e) => setBanReason(e.target.value)}
                />
                <ButtonContainer>
                  <ModalButton onClick={handleBanSubmit}>벤 완료</ModalButton>
                  <ModalButton onClick={handleModalClose}>취소</ModalButton>
                </ButtonContainer>
              </ModalContent>
            </Modal>
        )}
      </Container>
  );
};

export default Users;

// 스타일 컴포넌트 정의
const Container = styled.div`
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  max-width: 1000px;
  margin: 0 auto;
`;

const Table = styled.table`
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
`;

const Th = styled.th`
  background-color: #f1f3f5;
  padding: 8px;
  text-align: center;
  font-weight: bold;
  color: #495057;
`;

const Tr = styled.tr`
  cursor: pointer;
`;

const Td = styled.td`
  padding: 8px;
  text-align: center;
  color: #343a40;
`;

const ErrorText = styled.p`
  color: red;
  font-size: 14px;
  margin-top: 10px;
`;

const ProfileContainer = styled.div`
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  max-width: 800px;
  margin: 0 auto;
`;

const ProfileHeader = styled.div`
  display: flex;
  align-items: center;
  border-bottom: 1px solid #dee2e6;
  padding-bottom: 20px;
`;

const ProfileImage = styled.img`
  width: 150px;
  height: 150px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 20px;
`;

const ProfileInfo = styled.div`
  display: flex;
  flex-direction: column;
`;

const ProfileName = styled.h2`
  font-size: 1.8rem;
  color: #343a40;
`;

const ProfileBody = styled.div`
  margin-top: 20px;
`;

const SectionTitle = styled.h3`
  font-size: 1.5rem;
  color: #343a40;
  margin-bottom: 20px;
`;

const ProfileDetailTable = styled.table`
  width: 100%;
  margin-bottom: 20px;
`;

const DetailRow = styled.tr``;

const DetailTh = styled.th`
  text-align: left;
  padding: 10px;
  font-weight: bold;
  color: #495057;
  background-color: #f1f3f5;
  width: 30%;
`;

const DetailTd = styled.td`
  text-align: left;
  padding: 10px;
  background-color: #ffffff;
  color: #343a40;
  width: 70%;
`;

const ButtonContainer = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
`;

const BackButton = styled.button`
  padding: 10px 20px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #5a6268;
  }
`;

const BanButton = styled.button`
  padding: 10px 20px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #c82333;
  }
`;

const ModalContent = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
`;

const ModalButton = styled.button`
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #0056b3;
  }
`;
