package com.sparta.itsmine.domain.chat.repository;

import com.sparta.itsmine.domain.chat.entity.ChatRoom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByFromUserId(Long userId);

    Optional<ChatRoom> findByRoomId(String roodId);

}
