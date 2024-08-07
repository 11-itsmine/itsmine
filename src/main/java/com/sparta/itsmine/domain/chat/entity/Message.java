package com.sparta.itsmine.domain.chat.entity;

import com.sparta.itsmine.domain.chat.dto.MessageRequestDto;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Getter
@NoArgsConstructor
@DynamoDbBean
public class Message {

    private UUID id;
    private String roomId;
    private Long fromUserId;
    private String message;
    private LocalDateTime time;

    public Message(MessageRequestDto requestDto) {
        this.id = requestDto.getMessageId();
        this.roomId = requestDto.getRoomId();
        this.fromUserId = requestDto.getFromUserId();
        this.message = requestDto.getMessage();
        this.time = requestDto.getTime ();
    }

    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @DynamoDbAttribute("roomId")
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @DynamoDbAttribute("fromUserId")
    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    @DynamoDbAttribute("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @DynamoDbAttribute("time")
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
