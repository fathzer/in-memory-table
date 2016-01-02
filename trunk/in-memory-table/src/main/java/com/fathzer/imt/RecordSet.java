package com.fathzer.imt;

import com.fathzer.imt.util.IntIterator;

/** A set of records.
 * @author Jean-Marc Astesana
 */
public class RecordSet {
	private Bitmap bitmap;
	private TagsTable<?> table;

	RecordSet(Bitmap bitmap, TagsTable<?> table) {
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
	public TagsTable<?> getTable() {
		return table;
	}
}
