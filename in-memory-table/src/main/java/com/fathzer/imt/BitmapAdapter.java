package com.fathzer.imt;

import java.util.BitSet;
import java.util.Iterator;

import org.roaringbitmap.RoaringBitmap;

import com.fathzer.imt.implementation.BitSetAdapter;
import com.fathzer.imt.implementation.EWAH32BitMapAdapter;
import com.fathzer.imt.implementation.EWAH64BitmapAdapter;
import com.fathzer.imt.implementation.RoaringBitmapAdapter;
import com.fathzer.imt.util.IntIterator;
import com.googlecode.javaewah.EWAHCompressedBitmap;
import com.googlecode.javaewah32.EWAHCompressedBitmap32;

/** This interface allow to create table based on various bitmap implementations.
 * <br>It groups basic operations required on bitmaps.
 * @param <T> The underlying bitmap class
 */
public interface BitmapAdapter<T> {
	public static final BitmapAdapter<EWAHCompressedBitmap> EWAH64 = new EWAH64BitmapAdapter();
	public static final BitmapAdapter<EWAHCompressedBitmap32> EWAH32 = new EWAH32BitMapAdapter();
	public static final BitmapAdapter<RoaringBitmap> ROARING = new RoaringBitmapAdapter();
	public static final BitmapAdapter<BitSet> BITSET = new BitSetAdapter();

	/** Creates a new empty Bitmap.
	 * @return a new empty bitmap.
	 */
	T create();
	
	/** Creates a new bitmap and sets some bits.
	 * @param iter an iterator. Each bit returned by this iterator will be set.
	 */
	T create(IntIterator iter);
	
	/** Gets the cardinality of a bitmap.
	 * @param bitmap The bitmap.
	 * @return
	 */
	int getCardinality (T bitmap);
	
	/** Gets the union of two bitmaps.
	 * @param b1 first bitmap
	 * @param b2 second bitmap
	 * @return a new bitmap (no side effect with arguments).
	 */
	T or(T b1, T b2);
	
	/** Gets the union of many bitmaps.
	 * <br>This method could be optimized.
	 * @param bitmaps The bitmaps
	 * @return a new bitmap (no side effect with arguments).
	 */
	T or(Iterator<T> bitmaps);
	
	/** Gets the intersection of two bitmaps.
	 * @param b1 first bitmap
	 * @param b2 second bitmap
	 * @return a new bitmap (no side effect with arguments).
	 */
	T and(T b1, T b2);
	
	/** Gets the negation of a bitmap.
	 * @param bitmap The bitmap to negate
	 * @param size The length of the bitmap (bits after this length will not be set)
	 * @return a new bitmap (no side effect with arguments).
	 */
	T not(T bitmap, int size);
	
	/** Gets an iterator over bitmap's set bits.
	 * @param bitmap The bitmap
	 * @return An iterator over set bits. 
	 */
	IntIterator getIterator(T bitmap);
	
	/** Tests whether a bitmap is empty.
	 * @param bitmap A bitmap
	 * @return true if no bit is set.
	 */
	boolean isEmpty (T bitmap);
	
	/** Tests whether a bit is set in a bitmap.
	 * @param bitmap The bitmap
	 * @param index a bit index
	 * @return true if the bit <i>index</i> is set.
	 */
	boolean contains (T bitmap, int index);
	
	/** Sets a bit in a bitmap to 1.
	 * @param bitmap The bitmap
	 * @param index The index of the bit to set
	 */
	void add(T bitmap, int index);

	/** Set a bit in a bitmap to 0.
	 * @param bitmap The bitmap
	 * @param index The index of the bit to set to 0
	 */
	void remove(T bitmap, int index);
	
	/** Gets the memory size of the bitmap.
	 * @param bitmap The bitmap
	 * @return The number of bytes occupied by the bitmap in memory 
	 */
	long getSizeInBytes (T bitmap);
}
