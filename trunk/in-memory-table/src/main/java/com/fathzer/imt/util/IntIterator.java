package com.fathzer.imt.util;

import java.util.NoSuchElementException;

/** An iterator on int.
 */
public interface IntIterator {
	/** Tests whether the iterator has next element.
	 * @return true if elements remain.
	 */
	boolean hasNext();
	/** Gets the next element.
	 * @return an int.
	 * @throws NoSuchElementException if no element remains.
	 */
	int next();
}
