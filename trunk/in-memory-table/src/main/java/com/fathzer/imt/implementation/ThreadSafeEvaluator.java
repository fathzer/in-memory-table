package com.fathzer.imt.implementation;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.Evaluator;
import com.fathzer.imt.TagsTable;

public abstract class ThreadSafeEvaluator<T> implements Evaluator<T> {
	private ThreadLocal<Evaluator<T>> evaluator = new ThreadLocal<Evaluator<T>>() {
		@Override
		protected Evaluator<T> initialValue() {
			return buildUnsafeEvaluator();
		}
	};
	
	protected abstract Evaluator<T> buildUnsafeEvaluator();
	
	@Override
	public Bitmap evaluate(TagsTable<T> table, String expression, boolean failIfUnknown) {
		return evaluator.get().evaluate(table, expression, (Boolean)failIfUnknown);
	}
}
