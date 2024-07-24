package com.sparta.itsmine.domain.user.service;


import static com.sparta.itsmine.global.security.JwtProvider.AUTHORIZATION_HEADER;

import com.sparta.itsmine.domain.refreshtoken.RefreshTokenAdapter;
import com.sparta.itsmine.domain.user.dto.ProfileUpdateRequestDto;
import com.sparta.itsmine.domain.user.dto.SignupRequestDto;
import com.sparta.itsmine.domain.user.dto.UserResponseDto;
import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.domain.user.repository.UserAdapter;
import com.sparta.itsmine.domain.user.repository.UserRepository;
import com.sparta.itsmine.domain.user.utils.UserRole;
import com.sparta.itsmine.global.common.response.ResponseExceptionEnum;
import com.sparta.itsmine.global.exception.user.UserAlreadyExistsException;
import com.sparta.itsmine.global.exception.user.UserDeletedException;
import com.sparta.itsmine.global.exception.user.UserNotDeletedException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAdapter adapter;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenAdapter refreshTokenAdapter;
    private final UserAdapter userAdapter;

    // @Value("${admin.token}")
    // private String adminToken;
    @Transactional
    public String signup(SignupRequestDto requestDto) {
        if (adapter.existsByUsername(requestDto.getUsername())) {
            throw new UserAlreadyExistsException(ResponseExceptionEnum.USER_ALREADY_EXIST);
        }

        UserRole role = UserRole.USER;
        // 필요시 어드민 토큰 사용
        // if (requestDto.isAdmin()) {
        //     if (!requestDto.getAdminToken().equals(adminToken)) {
        //         throw new AdminTokenNotValidException(ResponseExceptionEnum.)
        //     }
        // }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User(requestDto.getUsername(), encodedPassword, requestDto.getName(),
                requestDto.getNickname(), requestDto.getEmail(), role, requestDto.getAddress());
        userRepository.save(user);
        return requestDto.getName();
    }

    public void logout(String username, HttpServletResponse response) {
        response.setHeader(AUTHORIZATION_HEADER, "");
        refreshTokenAdapter.deleteByUsername(username);
    }

    public UserResponseDto getUser(Long userId) {
        User user = userAdapter.findById(userId);
        return new UserResponseDto(user);
    }

    @Transactional
    public void withdraw(User user) {

        if (user.getDeletedAt() != null) {
            throw new UserDeletedException(ResponseExceptionEnum.USER_DELETED);
        }

        user.updateDeletedAt(LocalDateTime.now());
        userAdapter.save(user);
    }

    @Transactional
    public void resign(Long userId) {

        User user = userAdapter.findById(userId);
        if (user.getDeletedAt() == null) {
            throw new UserNotDeletedException(ResponseExceptionEnum.USER_NOT_DELETED);
        }

        user.updateDeletedAt(null);
        userAdapter.save(user);
    }

    @Transactional
    public void update(User user, ProfileUpdateRequestDto requestDto) {

        user.updateProfile(requestDto);
        userAdapter.save(user);
    }
}
