package com.sparta.itsmine.domain.chat.controller;

import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.CHAT_BLACKLIST_USER_ADD;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.CHAT_BLACKLIST_USER_CANCEL;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.CHAT_GET_MESSAGE_LIST;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.CHAT_GET_ROOM_LIST;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.CHAT_SUCCESS_CREATE;
import static com.sparta.itsmine.global.common.response.ResponseCodeEnum.CHAT_SUCCESS_ROOM_LEAVE;

import com.sparta.itsmine.domain.chat.dto.RoomInfoResponseDto;
import com.sparta.itsmine.domain.chat.dto.UserRequestDto;
import com.sparta.itsmine.domain.chat.entity.Message;
import com.sparta.itsmine.domain.chat.service.ChatService;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.global.common.response.HttpResponseDto;
import com.sparta.itsmine.global.common.response.ResponseUtils;
import com.sparta.itsmine.global.security.UserDetailsImpl;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/v1/chatrooms")
public class ChatRoomController {

    private final ChatService chatService;

    /**
     * 내가 지금 참여하고 있는 채팅방 리스트를 불러 옵니다.
     *
     * @param userDetails 인가된 본인 유저 정보
     */
    @GetMapping
    public ResponseEntity<HttpResponseDto> getChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        List<RoomInfoResponseDto> responseDtos = chatService.findAllRoom(userId);

        if (responseDtos == null) {
            responseDtos = new ArrayList<>();
        }

        return ResponseUtils.of(CHAT_GET_ROOM_LIST, responseDtos);
    }


    /**
     * 채팅방을 만듭니다..(상품의 대한 질문이나 실시간으로 확인 하고 싶을떄 1:1 방식으로 진행)
     * <p>
     * 판매자에게 채팅 요청 -> 채팅 수락 후 채팅방을 만듬 -> 만들때 유저 정보도 같이 들어감
     *
     * @param requestDto  다른 사람의 유저 정보
     * @param userDetails 인가된 본인 유저 정보
     */
    @PostMapping
    public ResponseEntity<HttpResponseDto> createRoom(@RequestBody UserRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        RoomInfoResponseDto responseDto = chatService.createChatRoom(user, requestDto.getUserId());
        return ResponseUtils.of(CHAT_SUCCESS_CREATE, responseDto);
    }

    /**
     * 내가 선택한 채팅방에 들어 갑니다. 들어갈때 이전 채팅 메시지 내역을 불러옵니다.
     *
     * @param roomId 채팅방 정보
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<HttpResponseDto> getChat(@PathVariable String roomId) {
        List<Message> messageList = chatService.getMessageList(roomId);
        return ResponseUtils.of(CHAT_GET_MESSAGE_LIST, messageList);
    }

    /**
     * 채팅방에서 나갑니다. 채팅방에 나가면은 남아있는 사람을 채팅을 못합니다.
     *
     * @param userDetails 인가된 본인 유저 정보
     * @param roomId      채팅방 정보
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<HttpResponseDto> leaveUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable String roomId
    ) {
        User user = userDetails.getUser();
        chatService.leaveUser(user, roomId);
        return ResponseUtils.of(CHAT_SUCCESS_ROOM_LEAVE);
    }

    /**
     * 블랙 리스트 등록 여부를 확인 합니다, 테이블에 데이터 있으면 삭제 없으면 등록
     *
     * @param userDetails 인가된 본인 유저 정보
     * @param requestDto  유저 정보가 들어있는 Dto
     */
    @PatchMapping("/blacklist")
    public ResponseEntity<HttpResponseDto> isBlackList(@RequestBody UserRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        boolean blackList = chatService.isBlackList(user, requestDto.getUserId());

        return blackList ? ResponseUtils.of(CHAT_BLACKLIST_USER_ADD)
                : ResponseUtils.of(CHAT_BLACKLIST_USER_CANCEL);
    }
}