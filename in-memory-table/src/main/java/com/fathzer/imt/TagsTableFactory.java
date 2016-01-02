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

	/** Builds an evaluator.
	 * @param table The table on which evalations will be made.
	 * @return An new evaluator.
	 */
	Evaluator buildEvaluator(TagsTable<T> table);

	/** Builds the object the maps tags to bitmaps.
	 * @return a new map.
	 */
	BitmapMap<T> buildmap();
}
