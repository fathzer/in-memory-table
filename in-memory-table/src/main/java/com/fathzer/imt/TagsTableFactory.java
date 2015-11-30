package com.fathzer.imt;

import java.util.Iterator;

import com.fathzer.soft.javaluator.AbstractEvaluator;

/** This interface allow to create table based on various bitmap implementations.
 * <br>It groups basic operations required on bitmaps.
 * @param <V> The underlying bitmap class
 */
public interface TagsTableFactory<T,V extends Bitmap> {
	/** Creates a new empty Bitmap.
	 * @return a new empty bitmap.
	 */
	V create();
	
	/** Gets the union of many bitmaps.
	 * <br>This method could be optimized.
	 * @param bitmaps The bitmaps
	 * @return a new bitmap (no side effect with arguments).
	 */
	V or(Iterator<V> bitmaps);

	AbstractEvaluator<V> buildEvaluator(TagsTable<T,V> table);

	//TODO Have a look and test https://github.com/OpenHFT/Chronicle-Map#what-is-the-difference-between-sharedhashmap-and-chroniclemap
	BitmapMap<T,V> buildmap();

	T stringToTag(String string);
}
