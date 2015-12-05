package com.fathzer.imt.implementation;

import com.fathzer.imt.TagsTable;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

public class DefaultEvaluator<T> extends AbstractLogicalEvaluator<T> {
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

	public DefaultEvaluator(TagsTable<T> table) {
		super(PARAMETERS, table);
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