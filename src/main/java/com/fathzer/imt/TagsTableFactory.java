package com.fathzer.imt;

/** A factory that allows to create table based on various bitmap implementations.
 * <br>It groups basic operations required on bitmaps.
 * @param <T> The type of the tags
 */
public interface TagsTableFactory<T> {
	/** Creates a new empty Bitmap.
	 * @return a new empty bitmap.
	 */
	Bitmap create();
	
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
