package com.sparta.itsmine.domain.report.schedule;

import com.sparta.itsmine.domain.user.entity.User;
import com.sparta.itsmine.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportSchedule {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void deleteBlackDate() {
        // 오늘 날짜를 가져옵니다.
        LocalDate today = LocalDate.now();
        // 차단된 날짜가 있는 모든 사용자를 가져옵니다.
        List<User> blockUsers = userRepository.findAllByBlockedAtIsNotNull();
        blockUsers.stream()
                .filter(user -> today.equals(user.getBlockedAt().toLocalDate()))
                .forEach(user -> {
                    user.setBlockedAt(null);
                    user.setBenReason(null);
                });
    }

}
