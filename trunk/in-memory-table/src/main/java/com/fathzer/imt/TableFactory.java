package com.fathzer.imt;

import com.fathzer.soft.javaluator.AbstractEvaluator;

public interface TableFactory<T,V> {
	public BitmapAdapter<V> buildBitmapAdapter();
	
	public AbstractEvaluator<V> buildEvaluator(Table<T,V> table);

	//TODO Have a look and test https://github.com/OpenHFT/Chronicle-Map#what-is-the-difference-between-sharedhashmap-and-chroniclemap
	public BitmapMap<T,V> buildmap();

	public T stringToTag(String string);
}
