package com.matchmaker.common.constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadConstants {

	private static ThreadPoolExecutor getDefaultFixed(int fixedSize) {
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(fixedSize);
	}

	public static final ThreadPoolExecutor SET_USER_MATCH_COUNT_BATCH_EXECUTOR = getDefaultFixed(30);
}
