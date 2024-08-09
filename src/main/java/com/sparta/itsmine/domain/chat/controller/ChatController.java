package com.sparta.itsmine.domain.chat.controller;

import com.sparta.itsmine.domain.chat.dto.MessageRequestDto;
import com.sparta.itsmine.domain.chat.service.ChatService;
import jakarta.jms.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;
    private final JmsTemplate jmsTemplate;
    private final Queue queue;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트가 /app/chat.message.{chatRoomId}로 메시지를 전송할 때 호출됩니다. 메시지를 ActiveMQ로 전송합니다.
     *
     * @param requestDto 메시지 보낼 정보를 담고 있는 DTO
     */
    @MessageMapping("chat.message/{chatRoomId}")
    public void sendMessage(@Payload MessageRequestDto requestDto) {
        log.info("Attempting to send message: {}", requestDto.getMessage());

        jmsTemplate.convertAndSend(queue, requestDto);

    }


    /**
     * ActiveMQ에서 메시지를 수신하여 WebSocket 클라이언트로 전달합니다. 수신한 메시지는 특정 채팅 방의 구독자에게 전송됩니다.
     *
     * @param requestDto 수신한 메시지를 담고 있는 DTO
     */
    @JmsListener(destination = "${activemq.queue.name}")
    public void receiveMessage(MessageRequestDto requestDto) {
        // 메시지를 데이터베이스에 저장하는 서비스 호출
        chatService.saveMessage(requestDto);
        log.info("여기 까지 오는지 확인: {}", requestDto.getMessage());
        // WebSocket 클라이언트에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat.message/" + requestDto.getRoomId(),
                requestDto);
    }
}
