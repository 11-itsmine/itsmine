package com.sparta.itsmine.global.lock;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.sparta.itsmine.global.lock.DistributedLock;
import com.sparta.itsmine.global.lock.LockManager;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {

	private final LockManager lockManager;

	@Around("@annotation(distributedLock)")
	public Object handleLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
		String dynamicKey = createDynamicKey(joinPoint, distributedLock.key());
		String lockName = distributedLock.prefix() + ":" + dynamicKey;
		return lockManager.lock(lockName, () -> {
			try {
				return joinPoint.proceed();
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		});
	}

	private String createDynamicKey(ProceedingJoinPoint joinPoint, String key) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature(); // 메서드 호출을 가로챔
		String[] parameterNames = methodSignature.getParameterNames(); // 파라미터 추출
		Object[] args = joinPoint.getArgs(); // 인자값 추출

		int index = Arrays.asList(parameterNames).indexOf(key);
		if (index == -1) {
			throw new IllegalArgumentException("올바르지 않은 키 : " + key);
		}

		return args[index].toString();  // 파라미터 값 반환
	}

}
