package com.fathzer.imt;

import java.util.Collection;
import java.util.Set;

public interface BitmapMap<T> {
	boolean containsKey(T key);
	Bitmap get(T key);
	Bitmap put(T key, Bitmap value);
	Set<T> keySet();
	Collection<Bitmap> values();
}
