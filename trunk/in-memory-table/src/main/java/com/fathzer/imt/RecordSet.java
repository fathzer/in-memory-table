package com.fathzer.imt;

import java.util.Iterator;

import com.fathzer.imt.util.IntIterator;

public class RecordSet<T> {
	private Bitmap bitmap;
	private TagsTable<T> table;

	RecordSet(Bitmap bitmap, TagsTable<T> table) {
		this.bitmap = bitmap;
		this.table = table;
	}
	
	/** Gets the number of records in the set.
	 * @return a positive integer.
	 */
	public int size() {
		return bitmap.getCardinality();
	}
	
	/** Gets the ids of records.
	 * @return an iterator
	 */
	public IntIterator getIds() {
		return bitmap.getIterator();
	}
	
	/** Gets the tags of a record.
	 * @param id The id of the record (as returned by {@link #getIds()} iterator).
	 * @return a new Iterator
	 */
	public Iterator<T> getTags(int id) {
		return table.getTags(id);
	}
}
