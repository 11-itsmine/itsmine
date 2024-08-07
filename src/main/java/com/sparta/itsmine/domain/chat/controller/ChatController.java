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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatService chatService;
    private final JmsTemplate jmsTemplate;
    private final Queue queue;
    private final SimpMessagingTemplate messagingTemplate;

    // 백프레셔를 위한 메시지 큐
    private final BlockingQueue<MessageRequestDto> messageQueue = new LinkedBlockingQueue<>(100); // 큐 크기를 100으로 제한

    // 메시지 ID를 저장하여 중복 체크를 수행하기 위한 맵
    private final ConcurrentMap<UUID, Boolean> processedMessages = new ConcurrentHashMap<>();

    /**
     * 클라이언트가 /app/chat.message.{chatRoomId}로 메시지를 전송할 때 호출됩니다.
     * 메시지를 ActiveMQ로 전송합니다.
     *
     * @param requestDto 메시지 보낼 정보를 담고 있는 DTO
     */
    @MessageMapping("chat.message/{chatRoomId}")
    public void sendMessage(@Payload MessageRequestDto requestDto) {
        log.info("Attempting to send message: {}", requestDto.getMessage());

        // 메시지 ID 설정
        if (requestDto.getMessageId() == null) {
            requestDto.setMessageId(UUID.randomUUID());
        }

        // 중복 메시지 여부 확인
        if (processedMessages.containsKey(requestDto.getMessageId())) {
            log.warn("Duplicate message detected, ignoring: {}", requestDto.getMessageId());
            return;
        }

        // 메시지를 큐에 추가
        try {
            boolean success = messageQueue.offer(requestDto, 2, TimeUnit.SECONDS); // 2초 동안 대기
            if (!success) {
                log.warn("Message queue is full, dropping message: {}", requestDto.getMessage());
                // 추가 처리 로직: 예를 들어, 메시지 드롭을 사용자에게 알림
                return;
            }

            // 큐에 성공적으로 추가된 경우, 실제 메시지 전송을 위한 스레드 실행
            processMessages();
        } catch (InterruptedException e) {
            log.error("Failed to add message to queue", e);
                Thread.currentThread().interrupt();
        }
    }

    /**
     * 메시지를 큐에서 가져와 ActiveMQ로 전송하고, WebSocket 클라이언트로 전달합니다.
     */
    @Async("messageExecutor")
    protected void processMessages() {
        // 새로운 스레드에서 메시지를 처리하여 비동기로 수행
        while (!messageQueue.isEmpty()) {
            try {
                MessageRequestDto requestDto = messageQueue.poll(2, TimeUnit.SECONDS); // 큐에서 메시지를 가져옴

                if (requestDto != null) {
                    log.info("Processing message: {}", requestDto.getMessage());

                    // 중복 메시지 여부 확인
                    if (processedMessages.putIfAbsent(requestDto.getMessageId(), true) != null) {
                        log.warn("Duplicate message detected during processing, ignoring: {}", requestDto.getMessageId());
                        continue;
                    }

                    // 메시지를 ActiveMQ로 전송
                    jmsTemplate.convertAndSend(queue, requestDto);

                    // WebSocket 클라이언트에게 메시지 전송
                    messagingTemplate.convertAndSend("/topic/chat.message/" + requestDto.getRoomId(), requestDto);
                    // 메시지를 데이터베이스에 저장하는 서비스 호출

                    chatService.saveMessage(requestDto);
                }
            } catch (Exception e) {
                log.error("Failed to process message", e);
            }
        }
    }

    /**
     * ActiveMQ에서 메시지를 수신하여 WebSocket 클라이언트로 전달합니다.
     * 수신한 메시지는 특정 채팅 방의 구독자에게 전송됩니다.
     *
     * @param requestDto 수신한 메시지를 담고 있는 DTO
     */
    @JmsListener(destination = "${activemq.queue.name}")
    public void receiveMessage(MessageRequestDto requestDto) {
        log.info("Received message: {}", requestDto.getMessage());

        // 중복 메시지 여부 확인
        if (processedMessages.putIfAbsent(requestDto.getMessageId(), true) != null) {
            log.warn("Duplicate message detected during receive, ignoring: {}", requestDto.getMessageId());
            return;
        }

        // 메시지를 데이터베이스에 저장하는 서비스 호출
        chatService.saveMessage(requestDto);

        // WebSocket 클라이언트에게 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat.message/" + requestDto.getRoomId(), requestDto);
    }
}
