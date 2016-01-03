package com.fathzer.imt;

/** An evaluator.
 * It evaluates expression and returns the result as a Bitmap.
 * <br><b>Warning</b>: Classes that implement this interface are supposed to be thread safe. 
 * @author JM Astesana
 */
public interface Evaluator {
	/** Evaluates an expression.
	 * @param expression The expression to evaluate
	 * @param failIfUnknown true if the method should fail if a tag is unknown, false if unknown tags should be assumed included in no records.
	 * @return A bitmap that contains the index of the records that satifies the expression.
	 * @throws UnknownTagException if the expression refers to unknown tags and <i>failIfUnknown</i> is true. Otherwise, Evaluator instances should not throw and exception but consider unknwon tags as use in no record.
	 */
	Bitmap evaluate(String expression, boolean failIfUnknown);
}
