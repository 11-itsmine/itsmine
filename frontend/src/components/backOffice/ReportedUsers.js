import React, {useEffect, useState} from 'react';
import axiosInstance from '../../api/axiosInstance';
import styled from 'styled-components';

const ReportedUsers = () => {
  const [reportedUsers, setReportedUsers] = useState([]);
  const [error, setError] = useState('');
  const [selectedReport, setSelectedReport] = useState(null); // 선택된 신고 정보 저장

  useEffect(() => {
    axiosInstance.get('/v1/report')
    .then(response => {
      setReportedUsers(response.data.data);
    })
    .catch(error => {
      setError('신고된 유저 목록을 불러오는 중 오류가 발생했습니다.');
    });
  }, []);

  const handleReportClick = (report) => {
    setSelectedReport(report);
  };

  const handleBackToList = () => {
    setSelectedReport(null);
  };

  return (
      <Container>
        <h2>신고된 유저 목록</h2>
        {error && <ErrorText>{error}</ErrorText>}
        {selectedReport ? (
            <DetailContainer>
              <DetailTitle>신고 상세 정보</DetailTitle>
              <DetailTable>
                <tbody>
                <DetailRow>
                  <DetailTh>신고자:</DetailTh>
                  <DetailTd>{selectedReport.username}</DetailTd>
                </DetailRow>
                <DetailRow>
                  <DetailTh>제목:</DetailTh>
                  <DetailTd>{selectedReport.title}</DetailTd>
                </DetailRow>
                <DetailRow>
                  <DetailTh>내용:</DetailTh>
                  <DetailTd>{selectedReport.content}</DetailTd>
                </DetailRow>
                <DetailRow>
                  <DetailTh>신고일자:</DetailTh>
                  <DetailTd>{new Date(
                      selectedReport.createdAt).toLocaleString()}</DetailTd>
                </DetailRow>
                <DetailRow>
                  <DetailTh>신고 상태:</DetailTh>
                  <DetailTd>{selectedReport.reportStatus}</DetailTd>
                </DetailRow>
                </tbody>
              </DetailTable>
              <BackButton onClick={handleBackToList}>목록으로 돌아가기</BackButton>
            </DetailContainer>
        ) : (
            <Table>
              <thead>
              <tr>
                <Th style={{width: '5%'}}>번호</Th>
                <Th style={{width: '40%'}}>제목</Th>
                <Th style={{width: '15%'}}>신고자</Th>
                <Th style={{width: '20%'}}>신고일자</Th>
                <Th style={{width: '20%'}}>신고 상태</Th>
              </tr>
              </thead>
              <tbody>
              {reportedUsers.map((report, index) => (
                  <Tr key={report.reportId}
                      onClick={() => handleReportClick(report)}>
                    <Td>{reportedUsers.length - index}</Td> {/* 번호를 역순으로 표시 */}
                    <Td>{report.title}</Td>
                    <Td>{report.username}</Td>
                    <Td>{new Date(report.createdAt).toLocaleString()}</Td>
                    <Td>{report.reportStatus}</Td>
                  </Tr>
              ))}
              </tbody>
            </Table>
        )}
      </Container>
  );
};

export default ReportedUsers;

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
  table-layout: fixed; /* 각 컬럼의 너비를 균등하게 배치 */
`;

const Th = styled.th`
  background-color: #f1f3f5;
  padding: 8px;
  text-align: center; /* 텍스트를 가운데 정렬 */
  font-weight: bold;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
`;

const Tr = styled.tr`
  cursor: pointer;

  &:nth-child(even) {
    background-color: #f8f9fa;
  }

  &:hover {
    background-color: #e9ecef;
  }
`;

const Td = styled.td`
  padding: 8px;
  text-align: center;
  color: #343a40;
  border: none; /* 테두리를 없앰 */
`;

const ErrorText = styled.p`
  color: red;
  font-size: 14px;
  margin-top: 10px;
`;

const DetailContainer = styled.div`
  margin-top: 20px;
  padding: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const DetailTitle = styled.h3`
  margin-bottom: 20px;
  font-size: 1.5rem;
  color: #343a40;
  border-bottom: 2px solid #dee2e6;
  padding-bottom: 10px;
`;

const DetailTable = styled.table`
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
  border: 1px solid #dee2e6;
  width: 20%; /* 너비를 20%로 줄임 */
`;

const DetailTd = styled.td`
  text-align: left;
  padding: 10px;
  border: 1px solid #dee2e6;
  background-color: #ffffff;
  color: #343a40;
  width: 80%; /* 너비를 80%로 확장 */
`;

const BackButton = styled.button`
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #6c757d; /* 어울리는 중립적인 색상 */
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;

  &:hover {
    background-color: #5a6268;
  }
`;
