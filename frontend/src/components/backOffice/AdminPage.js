import React, {useState} from 'react';
import styled from 'styled-components';
import ReportedUsers from './ReportedUsers';
import BannedUsers from './BannedUsers';

const AdminPage = () => {
  const [activeSection, setActiveSection] = useState('dashboard');

  return (
      <PageWrapper>
        <Sidebar>
          <Logo>관리자 페이지</Logo>
          <Menu>
            <MenuItem
                onClick={() => setActiveSection('dashboard')}>대시보드</MenuItem>
            <MenuItem onClick={() => setActiveSection('userManagement')}>유저
              관리</MenuItem>
            <MenuItem onClick={() => setActiveSection('reportedUsers')}>신고된
              유저</MenuItem>
          </Menu>
        </Sidebar>
        <MainContent>
          <Header>Admin Dashboard</Header>

          {activeSection === 'dashboard' && (
              <>
                <ContentSection>
                  <SectionTitle>신고된 유저 목록</SectionTitle>
                  <ReportedUsers/>
                </ContentSection>
                <ContentSection>
                  <SectionTitle>벤된 유저 목록</SectionTitle>
                  <BannedUsers/>
                </ContentSection>
              </>
          )}

          {activeSection === 'userManagement' && (
              <ContentSection>
                <SectionTitle>벤된 유저 목록</SectionTitle>
                <BannedUsers/>
              </ContentSection>
          )}

          {activeSection === 'reportedUsers' && (
              <ContentSection>
                <SectionTitle>신고된 유저 목록</SectionTitle>
                <ReportedUsers/>
              </ContentSection>
          )}
        </MainContent>
      </PageWrapper>
  );
};

export default AdminPage;

// 스타일링 정의
const PageWrapper = styled.div`
  display: flex;
  min-height: 100vh;
`;

const Sidebar = styled.div`
  width: 250px;
  background-color: #343a40;
  color: white;
  display: flex;
  flex-direction: column;
  padding: 20px;
`;

const Logo = styled.h2`
  color: white;
  text-align: center;
  margin-bottom: 40px;
`;

const Menu = styled.ul`
  list-style: none;
  padding: 0;
`;

const MenuItem = styled.li`
  padding: 10px 0;
  color: #adb5bd;
  cursor: pointer;

  &:hover {
    color: white;
  }
`;

const MainContent = styled.div`
  flex: 1;
  padding: 20px;
  background-color: #f8f9fa;
`;

const Header = styled.h1`
  font-size: 2rem;
  margin-bottom: 20px;
`;

const ContentSection = styled.div`
  margin-bottom: 40px;
  background-color: #ffffff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const SectionTitle = styled.h2`
  font-size: 1.5rem;
  margin-bottom: 20px;
`;
