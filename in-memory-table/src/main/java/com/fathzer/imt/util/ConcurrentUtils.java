package com.fathzer.imt.util;

import java.util.concurrent.ForkJoinPool;

public abstract class ConcurrentUtils {
	//Should use java 8 ForJoinPool.commonPool()
	private static final ForkJoinPool FJ_POOL = new ForkJoinPool();

	private ConcurrentUtils() {
		super();
	}

	public static ForkJoinPool getPool() {
		return FJ_POOL;
	}
}
