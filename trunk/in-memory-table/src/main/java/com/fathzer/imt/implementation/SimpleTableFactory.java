package com.fathzer.imt.implementation;

import java.util.HashMap;

import com.fathzer.imt.BitmapMap;
import com.fathzer.imt.BitmapAdapter;
import com.fathzer.imt.Table;
import com.fathzer.imt.TableFactory;
import com.fathzer.soft.javaluator.AbstractEvaluator;

/** A simple factory that uses a Hashmap and default evaluator.
 * @param <V> The bitmap type.
 */
public class SimpleTableFactory<V> implements TableFactory<String, V> {
	private BitmapAdapter<V> adapter;
	
	public SimpleTableFactory(BitmapAdapter<V> adapter) {
		this.adapter = adapter;
	}
	
	@Override
	public BitmapAdapter<V> buildBitmapAdapter() {
		return adapter;
	}

	@Override
	public AbstractEvaluator<V> buildEvaluator(Table<String, V> table) {
		return new DefaultEvaluator<String, V>(table, this);
	}

	@Override
	public BitmapMap<String, V> buildmap() {
		return new DefaultBitmapMap<String, V>();
	}

	@Override
	public String stringToTag(String string) {
		return string;
	}
	
	private static class DefaultBitmapMap<K,V> extends HashMap<K, V> implements BitmapMap<K, V> {
		private static final long serialVersionUID = 1L;
	}
}
