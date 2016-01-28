package com.fathzer.imt;

import java.util.Iterator;

/** A factory that allows to create table based on various bitmap implementations.
 * <br>It groups basic operations required on bitmaps.
 * @param <T> The type of the tags
 */
public interface TagsTableFactory<T> {
	/** Creates a new empty Bitmap.
	 * @return a new empty bitmap.
	 */
	Bitmap create();
	
	/** Gets the union of many bitmaps.
	 * <br>This method could be optimized.
	 * @param bitmaps The bitmaps
	 * @return a new bitmap (no side effect with arguments).
	 */
	Bitmap or(Iterator<Bitmap> bitmaps);

	/** Gets the evaluator used to evaluate the logical expressions on a tagsTable.
	 * <br><b>Warning</b>: The returned evaluator should be thread safe.
	 * @return The evaluator.
	 */
	Evaluator<T> getEvaluator();

	/** Builds the object the maps tags to bitmaps.
	 * @return a new map.
	 */
	BitmapMap<T> buildmap();
}
