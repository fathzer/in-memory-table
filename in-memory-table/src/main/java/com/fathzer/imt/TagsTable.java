package com.fathzer.imt;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
public class TagsTable<T, V extends Bitmap> implements Cloneable {
	private int size;
	private TagsTableFactory<T, V> factory;
	private BitmapMap<T,V> tagToBitmap;
	private ThreadLocal<AbstractEvaluator<V>> evaluator;
	private boolean isLocked;
	
	/** Creates a new empty table.
	 * @param adapter A RecordSetAdapter.
	 */
	public TagsTable(final TagsTableFactory<T,V> factory) {
		this.factory = factory;
		this.evaluator = new ThreadLocal<AbstractEvaluator<V>>() {
			@Override
			protected AbstractEvaluator<V> initialValue() {
				return factory.buildEvaluator(TagsTable.this);
			}
		};
		this.tagToBitmap = factory.buildmap();
		this.isLocked = false;
	}
	
	/** Adds some tags to this table.
	 * <br>Existing records are updated according to second arguments
	 * @param tags the tags to add to the table
	 * @param recordTags Each element of this list contains the existing record indexes where tag should be added, or null to not attached created tags to any records.
	 * @throws DuplicatedTagException if a tag is already declared in the table
	 * @throws IllegalArgumentException if a tagRecords is not null and has not same size as tags.
	 * @throws IllegalStateException if this is locked
	 */
	public void addTags(List<T> tags, List<IntIterator> tagRecords) {
		if (isLocked()) {
			throw new IllegalStateException();
		}
		if (tagRecords!=null && tags.size()!=tagRecords.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < tags.size(); i++) {
			T tag = tags.get(i);
			if (tagToBitmap.containsKey(tag)) {
				throw new DuplicatedTagException(tag.toString());
			}
			V bitmap = this.factory.create();
			if (tagRecords!=null) {
				while (tagRecords.get(i).hasNext()) {
					bitmap.add(tagRecords.get(i).next());
				}
			}
			tagToBitmap.put(tag, bitmap);
		}
	}
	
	/** Adds a record to the table.
	 * @param record an iterator on the tags contained in a record
	 * @param failIfUnknown true if the method should fail if a tag is unknown, false if unknown tags should be added automatically.
	 * @throws UnknownTagException if a tag is unknown and <i>failIfUnknown</i> is true.
	 * @throws IllegalStateException if this is locked
	 */
	public void addRecord(Iterator<T> record, boolean failIfUnknown) {
		if (isLocked()) {
			throw new IllegalStateException();
		}
		while (record.hasNext()) {
			T tag = record.next();
			V bitmap = tagToBitmap.get(tag);
			if (bitmap==null) {
				if (failIfUnknown) {
					throw new UnknownTagException(tag.toString());
				} else {
					bitmap = this.factory.create();
					tagToBitmap.put(tag, bitmap);
				}
			}
			bitmap.add(size);
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
		V bitmap = evaluator.get().evaluate(logicalExpr);
		return new RecordSet<T,V>((V) bitmap.getLocked(), this);
	}
	
	/** Gets the set of records having a tag.
	 * @param tag The tag
	 * @return a record set. <b>Warning:</b> There are side effects between the returned instance and the table, do not modify the record set !!!
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
		try {
			@SuppressWarnings("unchecked")
			TagsTable<T, V> result = (TagsTable<T, V>) super.clone();
			result.tagToBitmap = factory.buildmap();
			for (T key : this.tagToBitmap.keySet()) {
				IntIterator iterator = getBitMapIndex(key).getIterator();
				V freshBitmap = factory.create();
				while (iterator.hasNext()) {
					freshBitmap.add(iterator.next());
				}
				freshBitmap.trim();
				result.tagToBitmap.put(key, freshBitmap);
			}
			this.isLocked = false;
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Gets an immutable copy of a table.
	 * @return a new table. This method guarantees no side effect between this and the returned table.
	 */
	public TagsTable<T,V> getLocked() {
		if (isLocked) {
			return this;
		} else {
			@SuppressWarnings("unchecked")
			TagsTable<T, V> result = (TagsTable<T, V>) clone();
			result.tagToBitmap = factory.buildmap();
			Set<T> keys = tagToBitmap.keySet();
			for (T key : keys) {
				result.tagToBitmap.put(key, (V) tagToBitmap.get(key).getLocked());
			}
			result.isLocked = true;
			return result;
		}
	}
	
	/** Tests whether this table is immutable.
	 * @return true if the table is immutable.
	 */
	public boolean isLocked() {
		return isLocked;
	}
}
