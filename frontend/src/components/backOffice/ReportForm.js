import React, {useState} from "react";
import styled from "styled-components";

const ReportForm = ({onSubmit}) => {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [reportType, setReportType] = useState("SPAM"); // 기본 신고 유형 설정

  const handleSubmit = (e) => {
    e.preventDefault();
    const reportData = {
      title,
      content,
      reportType,
    };
    onSubmit(reportData);
  };

  return (
      <FormContainer onSubmit={handleSubmit}>
        <h2>신고하기</h2>
        <Input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="신고 제목"
            required
        />
        <Textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            placeholder="신고 내용을 입력하세요"
            required
        />
        <Select value={reportType}
                onChange={(e) => setReportType(e.target.value)}>
          <option value="SPAM">스팸</option>
          <option value="OFFENSIVE">모욕적인 내용</option>
          <option value="OTHER">기타</option>
        </Select>
        <SubmitButton type="submit">신고 접수</SubmitButton>
      </FormContainer>
  );
};

export default ReportForm;

// 스타일 컴포넌트 정의
const FormContainer = styled.form`
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  width: 300px;
`;

const Input = styled.input`
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
`;

const Textarea = styled.textarea`
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
`;

const Select = styled.select`
  width: 100%;
  padding: 10px;
  margin-bottom: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
`;

const SubmitButton = styled.button`
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;

  &:hover {
    background-color: #0056b3;
  }
`;
