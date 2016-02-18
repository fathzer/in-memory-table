package com.fathzer.imt.implementation;

import java.util.Iterator;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.Evaluator;
import com.fathzer.imt.TagsTable;
import com.fathzer.imt.UnknownTagException;
import com.fathzer.imt.util.IntIterator;
import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

/** A default logical evaluator based on the <a href="javaluator.fathzer.com">javaluator library</a>
 * <br><b>Warning</b>: This evaluator is not thread safe.
 * @author Jean-Marc Astesana
 * @param <T> The type of keys used in the tags table on which this evaluator works.
 */
public abstract class AbstractLogicalEvaluator<T> extends AbstractEvaluator<Bitmap> implements Evaluator<T> {
	/** Constructor.
	 * @param params The evaluator parameters (see <a href="javaluator.fathzer.com/en/doc/javadoc/com/fathzer/soft/javaluator/Parameters.html">Parameter</a>)
	 */
	protected AbstractLogicalEvaluator(Parameters params) {
		super(params);
	}
	
	@Override
	protected Bitmap toValue(String literal, Object context) {
		@SuppressWarnings("unchecked")
		Context ct = (Context) context;
		Bitmap result = ct.table.getBitMapIndex(stringToTag(literal));
		if (result==null) {
			if (ct.failIfUnknown) {
				throw new UnknownTagException(literal);
			} else {
				result = ct.table.getFactory().create();
			}
		}
		return result;
	}
	
	/** Converts a variable name found in a logical expression to a tag.
	 * @param variable The variable name
	 * @return the tag corresponding to that variable.
	 */
	protected abstract T stringToTag(String variable);

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
			@SuppressWarnings("unchecked")
			Context ct = (Context)evaluationContext;
			v1.not(ct.table.getSize());
		} else if (getOr().equals(operator)) {
			v1.or(v2);
		} else if (getAnd().equals(operator)) {
			v1.and(v2);
		} else {
			v1 = super.evaluate(operator, operands, evaluationContext);
		}
		return v1;
	}
	
	private final class Context {
		TagsTable<T> table;
		boolean failIfUnknown;
		Context(TagsTable<T> table, boolean failIfUnknown) {
			super();
			this.table = table;
			this.failIfUnknown = failIfUnknown;
		}
	}
	
	@Override
	public Bitmap evaluate(TagsTable<T> table, String expression, boolean failIfUnknown) {
		Bitmap result;
		if (expression.isEmpty()) {
			result = table.getFactory().create();
			result.not(table.getSize());
		} else {
			result = super.evaluate(expression, new Context(table, failIfUnknown));
			if (result instanceof Container) {
				result = ((Container) result).internal;
			}
		}
		return result;
	}

	/** Gets the NOT operator.
	 * @return the NOT operator.
	 */
	protected abstract Operator getNegate();

	/** Gets the AND operator.
	 * @return the and operator.
	 */
	protected abstract Operator getAnd();

	/** Gets the OR operator.
	 * @return the OR operator.
	 */
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
		@Override
		public String toString() {
			return internal.toString();
		}
	}
}
