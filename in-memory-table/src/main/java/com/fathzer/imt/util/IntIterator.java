package com.fathzer.imt.util;

import java.util.NoSuchElementException;

/** An iterator on int.
 */
public interface IntIterator {
	/** An empty iterator on int. 
	 */
	public static final IntIterator EMPTY = new IntIterator() {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public int next() {
			throw new NoSuchElementException();
		}
	};

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
