package com.sparta.itsmine.domain.chat.entity;

import com.sparta.itsmine.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "chat_room", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"roomId"}),
        @UniqueConstraint(columnNames = {"to_user_id"})
})
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @OneToOne
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JoinChat> joinChats = new ArrayList<>();

    public ChatRoom(User fromUser, User toUser) {
        this.roomId = UUID.randomUUID().toString();
        this.fromUser = fromUser;
        this.toUser = toUser;
        Stream.of(toUser, fromUser)
                .map(streamUser -> JoinChat.createChat(this, streamUser))
                .forEach(joinChats::add);
    }
}
