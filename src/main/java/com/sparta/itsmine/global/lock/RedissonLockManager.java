package com.sparta.itsmine.global.lock;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.sparta.itsmine.global.lock.LockManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedissonLockManager implements LockManager {

	private final RedissonClient redissonClient;
	private static final long WAIT_TIME = 10L;
	private static final long LEASE_TIME = 3L;
	private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

	@Override
	public <T> T lock(String lockName, Supplier<T> operation) {
		RLock lock = redissonClient.getFairLock(lockName);
		boolean isLocked = false;
		try {
			isLocked = lock.tryLock(WAIT_TIME, LEASE_TIME, TIME_UNIT);
			if (!isLocked) {
				throw new IllegalArgumentException("키를 얻을 수 없습니다.");
			}
			return operation.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // 인터럽트 상태 복구
			throw new IllegalArgumentException("락 중단됨", e);
		} finally {
			if (isLocked && lock.isHeldByCurrentThread()) {  // 현재 스레드가 락을 소유하고 있는지 확인
				lock.unlock();
			}
		}
	}
}
