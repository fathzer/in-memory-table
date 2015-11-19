package com.fathzer.imt;

import java.util.Iterator;
import java.util.List;

import com.fathzer.imt.util.IntIterator;
import com.fathzer.soft.javaluator.AbstractEvaluator;

/** A table is a set of records. Each of them has contains some tags and are identified by a positive or null integer.
 * <br>It is stored in memory using bitmap indexes in order to provide fast requests on logical expression on tags.
 * <br>Example: Let's say you have a vehicle set. Tags are "red", "green", "blue", "fast", "cheap", "electrical".
 * Requests can be "red && electrical" or "(fast && red) || (cheap)".
 * A request's result is a record set that can be accessed to get its cardinality and the tags of each of its records.
 * <br><br>
 * This class is not thread safe. Many threads can call {@link #evaluate(String)} concurrently, but not adding new tags or records.
 * <br>
 * @author JM Astesana
 * @param <T> The type of tags. This class should implements hashcode and equals in order to be used in a Map.
 * @param <V> The type of the RecordSet managed by this table.
 */
public class TagsTable<T, V> implements Cloneable {
	private int size;
	private TableFactory<T, V> factory;
	private BitmapMap<T,V> tagToBitmap;
	private ThreadLocal<AbstractEvaluator<V>> evaluator;
	private BitmapAdapter<V> adapter;
	
	/** Creates a new empty table.
	 * @param adapter A RecordSetAdapter.
	 */
	public TagsTable(final TableFactory<T,V> factory) {
		this.factory = factory;
		this.adapter = factory.buildBitmapAdapter();
		this.evaluator = new ThreadLocal<AbstractEvaluator<V>>() {
			@Override
			protected AbstractEvaluator<V> initialValue() {
				return factory.buildEvaluator(TagsTable.this);
			}
		};
		this.tagToBitmap = factory.buildmap();
	}
	
	/** Adds some tags to this table.
	 * <br>Existing records are updated according to second arguments
	 * @param tags the tags to add to the table
	 * @param recordTags Each element of this list contains the existing record indexes where tag should be added, or null to not attached created tags to any records.
	 * @throws DuplicatedTagException if a tag is already declared in the table
	 * @throws IllegalArgumentException if a tagRecords is not null and has not same size as tags.
	 */
	public void addTags(List<T> tags, List<IntIterator> tagRecords) {
		if (tagRecords!=null && tags.size()!=tagRecords.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < tags.size(); i++) {
			T tag = tags.get(i);
			if (tagToBitmap.containsKey(tag)) {
				throw new DuplicatedTagException(tag.toString());
			}
			tagToBitmap.put(tag, tagRecords==null?this.adapter.create():this.adapter.create(tagRecords.get(i)));
		}
	}
	
	/** Adds a record to the table.
	 * @param record an iterator on the tags contained in a record
	 * @param failIfUnknown true if the method should fail if a tag is unknown, false if unknown tags should be added automatically.
	 * @throws UnknownTagException if a tag is unknown and <i>failIfUnknown</i> is true.
	 */
	public void addRecord(Iterator<T> record, boolean failIfUnknown) {
		while (record.hasNext()) {
			T tag = record.next();
			V bitmap = tagToBitmap.get(tag);
			if (bitmap==null) {
				if (failIfUnknown) {
					throw new UnknownTagException(tag.toString());
				} else {
					bitmap = this.adapter.create();
					tagToBitmap.put(tag, bitmap);
				}
			}
			this.adapter.add(bitmap, size);
		}
		size++;
	}
	
	/** Gets the number of records contained in the table.
	 * @return an integer
	 */
	public int getSize() {
		return this.size;
	}

	/** Gets the set of records that verify a logical expression.
	 * @param logicalExpr a logical expression.
	 * <br>Supported operators are ! (not), && (and) and || (or).
	 * @return a record set
	 */
	public RecordSet<T,V> evaluate(String logicalExpr) {
		return new RecordSet<T,V>(evaluator.get().evaluate(logicalExpr), this);
	}
	
	/** Gets the set of records having a tag.
	 * @param tag The tag
	 * @return a record set. <b>Warning:</b> There are side effects between the returned instance and the table, do not modify the record set !!!
	 * <br>//TODO It would probably be better to have a mechanism to lock the table and make its recordSet immutable.
	 */
	public V getBitMapIndex(T tag) {
		return this.tagToBitmap.get(tag);
	}
	
	/** Clones the table.
	 * This method performs a deep clone and guarantees no side effect between this and the returned table.
	 * @return a new Table that contains a modifiable copy of this.
	 */
	@Override
	public Object clone() {
		@SuppressWarnings("unchecked")
		TagsTable<T, V> result = (TagsTable<T, V>) this.clone();
		result.tagToBitmap = factory.buildmap();
		for (T key : this.tagToBitmap.keySet()) {
			V freshBitmap = adapter.create(adapter.getIterator(getBitMapIndex(key)));
			result.tagToBitmap.put(key, freshBitmap);
		}
		return result;
	}
	
	BitmapAdapter<V> getAdapter() {
		return adapter;
	}
	
	/** Gets an immutable copy of a table.
	 * @return a new table. This method guarantees no side effect between this and the returned table
	 */
	public TagsTable<T,V> getLocked() {
		//FIXME
		return this; //TODO
	}
	
	public boolean isLocked() {
		return false; //TODO
	}
}
