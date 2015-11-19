package com.fathzer.imt.implementation;

import java.util.Iterator;

import com.fathzer.imt.BitmapAdapter;
import com.fathzer.imt.TagsTable;
import com.fathzer.imt.TableFactory;
import com.fathzer.imt.UnknownTagException;
import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

public abstract class AbstractLogicalEvaluator<T, V> extends AbstractEvaluator<V> {
  private final TagsTable<T, V> table;
	private TableFactory<T, V> factory;
	private BitmapAdapter<V> adapter;

	public AbstractLogicalEvaluator(Parameters params, TagsTable<T, V> table, TableFactory<T, V> factory) {
		super(params);
		this.table = table;
		this.factory = factory;
		adapter = factory.buildBitmapAdapter();
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
		V ope1 = operands.next();
		if (getNegate().equals(operator)) {
      return adapter.not(ope1, table.getSize());
    } else if (getOr().equals(operator)) {
      return adapter.or(ope1, operands.next());
    } else if (getAnd().equals(operator)) {
      return adapter.and(ope1, operands.next());
    } else {
      return super.evaluate(operator, operands, evaluationContext);
    }
  }

	protected abstract Operator getNegate();
	protected abstract Operator getAnd();
	protected abstract Operator getOr();
}
