import React, {useEffect, useRef, useState} from 'react';
import styled from 'styled-components';
import {Stomp} from '@stomp/stompjs';
import axiosInstance from '../../api/axiosInstance';
import {v4 as uuidv4} from 'uuid';

const ChatWindow = ({room, onClose}) => {
  const {
    roomId,
    userDetailId,
    fromUserId,
    fromUserNickname,
    toUserId,
    toUserNickname
  } = room;
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const stompClient = useRef(null);
  const messageListRef = useRef(null);

  useEffect(() => {
    if (!roomId) {
      console.error('Room ID is not available');
      return;
    }

    // 초기 메시지 로드
    const fetchMessages = async () => {
      try {
        const response = await axiosInstance.get(`/v1/chatrooms/${roomId}`);
        setMessages(response.data.data || []);
      } catch (error) {
        console.error('Failed to fetch messages:', error);
      }
    };

    fetchMessages();

    // 표준 WebSocket 및 STOMP 설정
    const socket = new WebSocket(
        'ws://52.79.213.8:8080/ws');
    stompClient.current = Stomp.over(socket);

    stompClient.current.connect(
        {},
        (frame) => {
          console.log('Connected: ' + frame);

          stompClient.current.subscribe(`/topic/chat.message/${roomId}`,
              (message) => {
                const newMsg = JSON.parse(message.body);
                setMessages((prevMessages) => [...prevMessages, newMsg]);
              });
        },
        (error) => {
          console.error('Connection error: ', error);
        }
    );

    return () => {
      disconnectWebSocket();
    };
  }, [roomId]);

  useEffect(() => {
    if (messageListRef.current) {
      messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
    }
  }, [messages]);

  const handleSendMessage = () => {
    if (newMessage.trim() === '') {
      return;
    }

    const messageObject = {
      messageId: uuidv4(),
      message: newMessage,
      fromUserId: userDetailId,
      roomId: roomId,
      time: new Date().toISOString(),
    };

    stompClient.current.send(`/app/chat.message/${roomId}`, {},
        JSON.stringify(messageObject));
    setNewMessage('');
  };

  const handleKeyPress = (event) => {
    if (event.key === 'Enter') {
      handleSendMessage();
      event.preventDefault();
    }
  };

  const disconnectWebSocket = () => {
    if (stompClient.current && stompClient.current.connected) {
      stompClient.current.disconnect(() => {
        console.log('Disconnected from WebSocket');
        stompClient.current = null;
      });
    }
  };

  const handleLeaveAndRefresh = async () => {
    try {
      await axiosInstance.delete(`/v1/chatrooms/${roomId}`);
      console.log('Successfully left the chat room.');
    } catch (error) {
      console.error('Failed to leave the chat room:', error);
    } finally {
      disconnectWebSocket();
      onClose();
    }
  };

  const otherUserNickname = fromUserId === userDetailId ? toUserNickname
      : fromUserNickname;

  return (
      <ChatWindowContainer>
        <Header>
          <h2>{otherUserNickname}와의 채팅</h2>
          <CloseButton onClick={handleLeaveAndRefresh}>채팅방 나가기</CloseButton>
        </Header>
        <MessageList ref={messageListRef}>
          {messages.map((msg, index) => (
              <MessageItem key={index}
                           isOwnMessage={msg.fromUserId === userDetailId}>
                <strong>
                  {msg.fromUserId === userDetailId
                      ? '나'
                      : msg.fromUserId === fromUserId
                          ? fromUserNickname
                          : toUserNickname}
                  :
                </strong>{' '}
                {msg.message}
              </MessageItem>
          ))}
        </MessageList>
        <MessageInputContainer>
          <MessageInput
              type="text"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              placeholder="메시지를 입력하세요..."
              onKeyPress={handleKeyPress}
          />
          <SendButton onClick={handleSendMessage}>보내기</SendButton>
        </MessageInputContainer>
      </ChatWindowContainer>
  );
};

export default ChatWindow;

// 스타일링 정의
const ChatWindowContainer = styled.div`
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
`;

const Header = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
`;

const CloseButton = styled.button`
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #c82333;
  }
`;

const MessageList = styled.ul`
  list-style-type: none;
  padding: 0;
  margin-bottom: 20px;
  max-height: 400px;
  overflow-y: auto;
`;

const MessageItem = styled.li`
  padding: 10px;
  margin-bottom: 10px;
  background-color: ${(props) =>
      props.isOwnMessage ? '#daf8e3' : '#f1f1f1'};
  border-radius: 5px;
  text-align: ${(props) =>
      props.isOwnMessage ? 'right' : 'left'};
`;

const MessageInputContainer = styled.div`
  display: flex;
`;

const MessageInput = styled.input`
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 10px;
`;

const SendButton = styled.button`
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #0056b3;
  }
`;
