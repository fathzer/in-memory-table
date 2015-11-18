package com.fathzer.imt.implementation;

import com.fathzer.imt.Table;
import com.fathzer.imt.TableFactory;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

public class DefaultEvaluator<T, V> extends AbstractLogicalEvaluator<T,V> {
	/** The negate unary operator.*/
  private final static Operator NEGATE = new Operator("!", 1, Operator.Associativity.RIGHT, 3);
  /** The logical AND operator.*/
  private static final Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 2);
  /** The logical OR operator.*/
  private final static Operator OR = new Operator("||", 2, Operator.Associativity.LEFT, 1);
  private static final Parameters PARAMETERS;

	static {
    // Create the evaluator's parameters
    PARAMETERS = new Parameters();
    // Add the supported operators
    PARAMETERS.add(AND);
    PARAMETERS.add(OR);
    PARAMETERS.add(NEGATE);
    PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
	}

	public DefaultEvaluator(Table<T, V> table, TableFactory<T, V> factory) {
		super(PARAMETERS, table, factory);
	}

	@Override
	protected Operator getNegate() {
		return NEGATE;
	}

	@Override
	protected Operator getAnd() {
		return AND;
	}

	@Override
	protected Operator getOr() {
		return OR;
	}
}