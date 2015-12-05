package com.fathzer.imt.implementation;

import java.util.Iterator;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.TagsTable;
import com.fathzer.imt.UnknownTagException;
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
		Bitmap result = this.table.getBitMapIndex(table.getFactory().stringToTag(literal));
		if (result==null) {
			throw new UnknownTagException(literal);
		}
		return result;
	}

	@Override
	protected Bitmap evaluate(Operator operator, Iterator<Bitmap> operands, Object evaluationContext) {
		Bitmap result = operands.next();
		if (getNegate().equals(operator)) {
			return result.not(table.getSize());
		} else if (getOr().equals(operator)) {
			return result.or(operands.next());
		} else if (getAnd().equals(operator)) {
			return result.and(operands.next());
		} else {
			return super.evaluate(operator, operands, evaluationContext);
		}
	}

	protected abstract Operator getNegate();
	protected abstract Operator getAnd();
	protected abstract Operator getOr();
}
