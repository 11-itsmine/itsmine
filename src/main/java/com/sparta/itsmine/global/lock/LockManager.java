package com.sparta.itsmine.global.lock;

import java.util.function.Supplier;

public interface LockManager {
	<T> T lock(String lockName, Supplier<T> operation);
}
