package com.fathzer.imt;

import java.util.Set;

public interface BitmapMap<T,V> {
	boolean containsKey(T key);
	V get(T key);
	V put(T key, V value);
	Set<T> keySet();
}
