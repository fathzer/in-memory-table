package com.fathzer.imt;

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
	
	/** Gets this record's table.
	 * @return The table the record set on which the record set was built.
	 */
	public TagsTable<T> getTable() {
		return table;
	}
}
