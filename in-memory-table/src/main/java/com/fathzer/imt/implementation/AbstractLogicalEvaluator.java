package com.fathzer.imt.implementation;

import java.util.Iterator;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.TagsTable;
import com.fathzer.imt.UnknownTagException;
import com.fathzer.imt.util.IntIterator;
import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

public abstract class AbstractLogicalEvaluator<T> extends AbstractEvaluator<Bitmap> {
	private final TagsTable<T> table;

	public AbstractLogicalEvaluator(Parameters params, TagsTable<T> table) {
		super(params);
		this.table = table;
	}
	
	@Override
	protected Bitmap toValue(String literal, Object evaluationContext) {
		Bitmap result = this.table.getBitMapIndex(this.table.getFactory().stringToTag(literal));
		if (result==null) {
			throw new UnknownTagException(literal);
		}
		return result;
	}

	@Override
	protected Bitmap evaluate(Operator operator, Iterator<Bitmap> operands, Object evaluationContext) {
		Bitmap v1 = operands.next();
		Bitmap v2 = operands.hasNext() ? operands.next() : null;
		if (v1 instanceof Container) {
			if (v2 instanceof Container) {
				v2 = ((Container)v2).internal;
			}
		} else if (v2 instanceof Container) {
			Bitmap dummy = v1;
			v1 = v2;
			v2 = dummy;
		} else {
			v1 = new Container(v1);
		}
		if (getNegate().equals(operator)) {
			v1.not(table.getSize());
		} else if (getOr().equals(operator)) {
			v1.or(v2);
		} else if (getAnd().equals(operator)) {
			v1.and(v2);
		} else {
			v1 = super.evaluate(operator, operands, evaluationContext);
		}
		return v1;
	}
	
	@Override
	public Bitmap evaluate(String expression, Object evaluationContext) {
		Bitmap result = super.evaluate(expression, evaluationContext);
		if (result instanceof Container) {
			result = ((Container) result).internal;
		}
		return result;
	}

	protected abstract Operator getNegate();
	protected abstract Operator getAnd();
	protected abstract Operator getOr();
	
	private static class Container implements Bitmap {
		Bitmap internal;
		Container(Bitmap bitmap) {
			internal = bitmap.clone();
		}
		@Override
		public int getCardinality() {
			return internal.getCardinality();
		}
		@Override
		public void or(Bitmap bitmap) {
			internal.or(bitmap);
		}
		@Override
		public void xor(Bitmap bitmap) {
			internal.xor(bitmap);
		}
		@Override
		public void and(Bitmap bitmap) {
			internal.and(bitmap);			
		}
		@Override
		public void andNot(Bitmap bitmap) {
			internal.and(bitmap);
		}
		@Override
		public void not(int size) {
			internal.not(size);
		}
		@Override
		public void trim() {
			internal.trim();
		}
		@Override
		public IntIterator getIterator() {
			return internal.getIterator();
		}
		@Override
		public boolean isEmpty() {
			return internal.isEmpty();
		}
		@Override
		public boolean contains(int index) {
			return internal.contains(index);
		}
		@Override
		public void add(int index) {
			internal.add(index);
		}
		@Override
		public void remove(int index) {
			internal.remove(index);
		}
		@Override
		public long getSizeInBytes() {
			return internal.getSizeInBytes();
		}
		@Override
		public Bitmap getLocked() {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean isLocked() {
			return false;
		}
		@Override
		public Bitmap clone() {
			throw new UnsupportedOperationException();
		}
	}
}
