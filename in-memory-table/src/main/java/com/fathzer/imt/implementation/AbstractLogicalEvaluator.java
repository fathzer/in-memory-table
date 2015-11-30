package com.fathzer.imt.implementation;

import java.util.Iterator;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.TagsTable;
import com.fathzer.imt.TagsTableFactory;
import com.fathzer.imt.UnknownTagException;
import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

public abstract class AbstractLogicalEvaluator<T, V extends Bitmap> extends AbstractEvaluator<V> {
	private final TagsTable<T, V> table;
	private TagsTableFactory<T, V> factory;

	public AbstractLogicalEvaluator(Parameters params, TagsTable<T, V> table, TagsTableFactory<T, V> factory) {
		super(params);
		this.table = table;
		this.factory = factory;
	}
	
	@Override
	protected V toValue(String literal, Object evaluationContext) {
		V result = this.table.getBitMapIndex(this.factory.stringToTag(literal));
		if (result==null) {
			throw new UnknownTagException(literal);
		}
		return result;
	}

	@Override
	protected V evaluate(Operator operator, Iterator<V> operands, Object evaluationContext) {
		@SuppressWarnings("unchecked")
		V result = (V) operands.next().clone();
		if (getNegate().equals(operator)) {
			result.not(table.getSize());
		} else if (getOr().equals(operator)) {
			result.or(operands.next());
		} else if (getAnd().equals(operator)) {
			result.and(operands.next());
		} else {
			return super.evaluate(operator, operands, evaluationContext);
		}
		return result;
	}

	protected abstract Operator getNegate();
	protected abstract Operator getAnd();
	protected abstract Operator getOr();
}
