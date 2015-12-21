package com.fathzer.imt;

import com.fathzer.imt.util.IntIterator;

public interface Bitmap extends Cloneable {
	/** Gets the cardinality of this bitmap.
	 * @return a positive or null integer
	 */
	int getCardinality();
	
	/** Makes the union of this bitmap with another one.
	 * @param bitmap second bitmap
	 * @return a new Bitmap
	 */
	Bitmap or(Bitmap bitmap);
		
	/** Makes the intersection of this bitmap with another one.
	 * @param bitmap second bitmap
	 * @return a new Bitmap
	 */
	Bitmap and(Bitmap bitmap);
	
	/** Negates this bitmap.
	 * @param size The length of the bitmap (bits after this length will not be set)
	 * @return a new Bitmap
	 */
	Bitmap not(int size);
	
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
	
	/** Sets a bit in this bitmap to 1.
	 * @param index The index of the bit to set
	 */
	void add(int index);

	/** Set a bit in this bitmap to 0.
	 * @param index The index of the bit to set to 0
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
