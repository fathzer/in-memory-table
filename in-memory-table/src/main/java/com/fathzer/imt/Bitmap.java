package com.fathzer.imt;

import com.fathzer.imt.util.IntIterator;

/** A Bitmap (a compact representation of a sorted array of integers).
 * @author Jean-Marc Astesana
 */
public interface Bitmap extends Cloneable {
	/** Gets the cardinality of this bitmap.
	 * @return a positive or null integer
	 */
	int getCardinality();
	
	/** Performs the union of this bitmap with another one.
	 * <br>This bitmap is modified.
	 * @param bitmap second bitmap
	 * @throws IllegalStateException if this bitmap is locked
	 */
	void or(Bitmap bitmap);
	
	/** Performs a logical XOR of this bitmap with another one.
	 * <br>This bitmap is modified.
	 * @param bitmap second bitmap
	 * @throws IllegalStateException if this bitmap is locked
	 */
	void xor(Bitmap bitmap);
		
	/** Performs the intersection of this bitmap with another one.
	 * <br>This bitmap is modified.
	 * @param bitmap second bitmap
	 * @throws IllegalStateException if this bitmap is locked
	 */
	void and(Bitmap bitmap);
	
	/** Clears all of the bits in this Bitmap whose corresponding bit is set in another one.
	 * @param bitmap second bitmap
	 * @throws IllegalStateException if this bitmap is locked
	 */
	void andNot(Bitmap bitmap);
	
	/** Negates this bitmap.
	 * @param size The length of the bitmap (bits after this length will not be set)
	 * @throws IllegalStateException if this bitmap is locked
	 */
	void not(int size);
	
	/**  Recovers allocated but unused memory
	 */
	void trim();
	
	/** Gets an iterator over this bitmap's set bits.
	 * @return An iterator over set bits. 
	 */
	IntIterator getIterator();
	
	/** Tests whether this bitmap is empty.
	 * @return true if no bit is set.
	 */
	boolean isEmpty ();
	
	/** Tests whether a bit is set in this bitmap.
	 * @param index a bit index
	 * @return true if the bit <i>index</i> is set.
	 */
	boolean contains (int index);
	
	/** Tests whether this bitmap intersects another one.
	 * @param bitmap a Bitmap
	 * @return true if the bitmaps intersects (their and is not empty).
	 */
	boolean intersects(Bitmap bitmap);
	
	/** Sets a bit in this bitmap to 1.
	 * @param index The index of the bit to set
	 * @throws IllegalStateException if this bitmap is locked
	 */
	void add(int index);

	/** Set a bit in this bitmap to 0.
	 * @param index The index of the bit to set to 0
	 * @throws IllegalStateException if this bitmap is locked
	 */
	void remove(int index);
	
	/** Gets the memory size of the bitmap.
	 * @return The number of bytes occupied by the bitmap in memory 
	 */
	long getSizeInBytes ();
	
	/** Gets an immutable copy of this.
	 * @return a new bitmap if this is mutable or this if it is immutable. This method guarantees no side effect between this and the returned bitmap.
	 */
	Bitmap getLocked();
	
	/** Tests whether this bitmap is immutable.
	 * @return true if the bitmap is immutable.
	 */
	boolean isLocked();
	
	/** Clones this.
	 * @return A <b>mutable</b> copy of this. 
	 */
	Bitmap clone();
}
