package com.sparta.itsmine.domain.user.repository;

import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.global.common.response.ResponseExceptionEnum;
import com.sparta.itsmine.global.exception.user.UserNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAdapter {

    private final UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        ResponseExceptionEnum.USER_NOT_FOUND));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        ResponseExceptionEnum.USER_NOT_FOUND));
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean isDeleted(String username) {
        User user = findByUsername(username);
        if (user.getDeletedAt() != null) {
            return true;
        }
        return false;
    }

    public void delete(User user) {
        userRepository.delete(user);
    }
}
