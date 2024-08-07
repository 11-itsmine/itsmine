package com.sparta.itsmine.domain.chat.entity;

import com.sparta.itsmine.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "chat_room", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"roomId"}),
        @UniqueConstraint(columnNames = {"to_user_id"})
},indexes = @Index(name = "idx_room_id",columnList = "roomId")
)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomId;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User fromUser;

    @Enumerated(EnumType.STRING)
    private ChatStatus fromUserStatus;

    @OneToOne
    @JoinColumn(name = "to_user_id")
    private User toUser;

    @Enumerated(EnumType.STRING)
    private ChatStatus toUserStatus;
    
    public ChatRoom(User fromUser, User toUser) {
        this.roomId = UUID.randomUUID().toString();
        this.fromUser = fromUser;
        this.fromUserStatus = ChatStatus.TALK;
        this.toUser = toUser;
        this.toUserStatus = ChatStatus.TALK;
    }


    public void userStatusUpdate(User user) {
        if (user.getId().equals(fromUser.getId())) {
            this.fromUserStatus = ChatStatus.END;
        } else {
            this.toUserStatus = ChatStatus.END;
        }
    }
}
