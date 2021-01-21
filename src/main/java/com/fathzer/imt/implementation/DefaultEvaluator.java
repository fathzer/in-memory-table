package com.fathzer.imt.implementation;

import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Constant;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Parameters;

/** A default logical AbstractLogicalEvaluator that uses ! as NOT operator, &amp;&amp; as AND and || as OR.
 * @author Jean-Marc Astesana
 * @param <T> The type of the table tags.
 */
public abstract class DefaultEvaluator<T> extends AbstractLogicalEvaluator<T> {
	/** The negate unary operator.*/
	private static final Operator NEGATE = new Operator("!", 1, Operator.Associativity.RIGHT, 3);
	/** The logical AND operator. */
	private static final Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 2);
	/** The logical OR operator. */
	private static final Operator OR = new Operator("||", 2, Operator.Associativity.LEFT, 1);
	private static final Constant ALWAYS_TRUE = new Constant("ALWAYS_TRUE");
	private static final Constant ALWAYS_FALSE = new Constant("ALWAYS_FALSE");
	private static final Parameters PARAMETERS;

	static {
		// Create the evaluator's parameters
		PARAMETERS = new Parameters();
		// Add the supported operators
		PARAMETERS.add(AND);
		PARAMETERS.add(OR);
		PARAMETERS.add(NEGATE);
		PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
		// Add the supported constants
		PARAMETERS.add(ALWAYS_TRUE);
		PARAMETERS.add(ALWAYS_FALSE);
	}

	/** Constructor.
	 */
	public DefaultEvaluator() {
		super(PARAMETERS);
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

	@Override
	protected Constant getAlwaysTrue() {
		return ALWAYS_TRUE;
	}

	@Override
	protected Constant getAlwaysFalse() {
		return ALWAYS_FALSE;
	}
}