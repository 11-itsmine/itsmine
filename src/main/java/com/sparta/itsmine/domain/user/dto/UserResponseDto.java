package com.sparta.itsmine.domain.user.dto;

import com.sparta.itsmine.domain.user.entity.User;
import java.util.List;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private Long userId;
    private String username;
    private String name;
    private String nickname;
    private String email;
    private String address;
    private List<String> imageUrls;

    public UserResponseDto(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.imageUrls = user.getImageUrls();
    }
}
