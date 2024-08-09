package com.sparta.itsmine.domain.user.entity;

import com.sparta.itsmine.domain.images.entity.Images;
import com.sparta.itsmine.domain.user.dto.ProfileUpdateRequestDto;
import com.sparta.itsmine.domain.user.utils.UserRole;
import com.sparta.itsmine.global.common.TimeStamp;
import com.sparta.itsmine.global.common.response.ResponseExceptionEnum;
import com.sparta.itsmine.global.exception.DataDuplicatedException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends TimeStamp {

    /**
     * 컬럼 - 연관관계 컬럼을 제외한 컬럼을 정의합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Column(nullable = false)
    private String address;

    private LocalDateTime deletedAt;

    private LocalDateTime blockedAt;

    private String benReason;

    private Long kakaoId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Images> imagesList = new ArrayList<>();

//    @OneToMany(mappedBy = "user")
//    private List<JoinChat> joinChatList;

    /**
     * 생성자 - 약속된 형태로만 생성가능하도록 합니다.
     */
    @Builder
    public User(String username, String encodedPassword, String name, String nickname, String email,
            UserRole role, String address, Long kakaoId) {
        this.username = username;
        this.password = encodedPassword;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.userRole = role;
        this.address = address;
        this.kakaoId = kakaoId;
    }

    public User(String username, String encodedPassword, String name, String nickname, String email,
            UserRole role, String address) {
        this.username = username;
        this.password = encodedPassword;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.userRole = role;
        this.address = address;
    }

    /**
     * 연관관계 - Foreign Key 값을 따로 컬럼으로 정의하지 않고 연관 관계로 정의합니다.
     */

    /**
     * 연관관계 편의 메소드 - 반대쪽에는 연관관계 편의 메소드가 없도록 주의합니다.
     */

    /**
     * 서비스 메소드 - 외부에서 엔티티를 수정할 메소드를 정의합니다. (단일 책임을 가지도록 주의합니다.)
     */

    public void updateDeletedAt(LocalDateTime date) {
        this.deletedAt = date;
    }

    public void updateProfile(ProfileUpdateRequestDto requestDto) {
        this.email = requestDto.getEmail();
        this.nickname = requestDto.getNickname();
        this.address = requestDto.getAddress();
    }

    public List<String> getImageUrls() {
        return imagesList.stream()
                .map(Images::getImagesUrl)
                .collect(Collectors.toList());
    }

    public User kakaoIdUpdate(Long kakaoId) {
        this.kakaoId = kakaoId;
        return this;
    }

    public void block(LocalDateTime blockDate, String benReason) {
        this.blockedAt = blockDate;
        this.benReason = benReason;
    }

    public void checkBlock() {
        if (blockedAt != null) {
            throw new DataDuplicatedException(ResponseExceptionEnum.USER_BEN);
        }
    }
}
