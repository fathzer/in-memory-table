package com.fathzer.imt;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.fathzer.imt.util.IntIterator;
import com.fathzer.imt.util.UnexpectedCloneNotSupportedException;

/** A table is a set of records. Each of them has contains some tags and are identified by a positive or null integer.
 * <br>It is stored in memory using bitmap indexes in order to provide fast requests on logical expression on tags.
 * <br>Example: Let's say you have a vehicle set. Tags are "red", "green", "blue", "fast", "cheap", "electrical".
 * Requests can be "red &amp;&amp; electrical" or "(fast &amp;&amp; red) || (cheap)".
 * A request's result is a record set that can be accessed to get its cardinality and the tags of each of its records.
 * <br><br>
 * This class is not thread safe. Many threads can call {@link #evaluate(String, boolean)} concurrently, but not adding new tags or records.
 * <br>
 * @author JM Astesana
 * @param <T> The type of tags. This class should implements hashcode and equals in order to be used in a Map.
 */
public class TagsTable<T> implements Cloneable {
	private final class TagsIterator implements Iterator<T> {
		private T next;
		private Iterator<T> iter;
		private int id;
		
		public TagsIterator(int id) {
			this.id = id;
			iter = tagToBitmap.keySet().iterator();
			findNext();
		}

		private void findNext() {
			next=null;
			while (next==null && iter.hasNext()) {
				T key = iter.next();
				if (tagToBitmap.get(key).contains(id)) {
					next = key;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return next!=null;
		}

		@Override
		public T next() {
			if (next==null) {
				throw new NoSuchElementException();
			} else {
				T result = next;
				findNext();
				return result;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private int size;
	private int logicalSize;
	private TagsTableFactory<T> factory;
	private Bitmap deletedRecords;
	private BitmapMap<T> tagToBitmap;
	private Evaluator evaluator;
	private boolean isLocked;
	
	/** Creates a new empty table.
	 * @param factory the factory used to build the table.
	 */
	public TagsTable(final TagsTableFactory<T> factory) {
		this.factory = factory;
		this.deletedRecords = factory.create();
		this.evaluator = factory.buildEvaluator(TagsTable.this);
		this.tagToBitmap = factory.buildmap();
		this.isLocked = false;
		this.logicalSize = 0;
		this.size = 0;
	}
	
	/** Adds some tags to this table.
	 * <br>Existing records are updated according to second arguments
	 * @param tags the tags to add to the table
	 * @param tagRecords Each element of this list contains the existing record indexes where tag should be added, or null to not attached created tags to any records.
	 * @throws DuplicatedTagException if a tag is already declared in the table
	 * @throws IllegalArgumentException if a tagRecords is not null and has not same size as tags.
	 * @throws IllegalStateException if this is locked
	 */
	public void addTags(List<T> tags, List<IntIterator> tagRecords) {
		check();
		if (tagRecords!=null && tags.size()!=tagRecords.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < tags.size(); i++) {
			T tag = tags.get(i);
			if (tagToBitmap.containsKey(tag)) {
				throw new DuplicatedTagException(tag.toString());
			}
			Bitmap bitmap = this.factory.create();
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
	 * @return the index of the added record in the table.
	 * @throws UnknownTagException if a tag is unknown and <i>failIfUnknown</i> is true.
	 * @throws IllegalStateException if this is locked
	 */
	public int addRecord(Iterator<T> record, boolean failIfUnknown) {
		check();
		int index;
		if (logicalSize<size) {
			// If table contains some deleted records, replace deleted record by new one.
			index = deletedRecords.getIterator().next();
			deletedRecords.remove(index);
		} else {
			// Add record at the end of the table
			index = size;
			size++;
		}
		while (record.hasNext()) {
			T tag = record.next();
			Bitmap bitmap = tagToBitmap.get(tag);
			if (bitmap==null) {
				if (failIfUnknown) {
					throw new UnknownTagException(tag.toString());
				} else {
					bitmap = this.factory.create();
					tagToBitmap.put(tag, bitmap);
				}
			}
			bitmap.add(index);
		}
		logicalSize++;
		return index;
	}

	private void check() {
		if (isLocked()) {
			throw new IllegalStateException();
		}
	}
	
	/** Deletes a record.
	 * @param index The record index (returned by method {@link #addRecord(Iterator, boolean)} or by a iterator on a {@link Bitmap}
	 * @throws IllegalArgumentException if index is negative or greater than or equals to size.
	 */
	public void deleteRecord(int index) {
		check();
		if (index>=size || index<0) {
			throw new IllegalArgumentException();
		}
		for (Bitmap bitmap:tagToBitmap.values()) {
			bitmap.remove(index);
		}
		logicalSize--;
		if (index==size-1) {
			size--;
		} else {
			deletedRecords.add(index);
		}
	}
	
	/** Gets the number of records contained in the table, including deleted records.
	 * <br>No record can have an index greater than or equals to @{link #getSize())
	 * @return an integer
	 */
	public int getSize() {
		return this.size;
	}
	
	/** Gets the number of records contained in the table, excluding deleted records.
	 * <br>Records can have an index greater than or equals to @{link #getLogicalSize())
	 * @return an integer
	 */
	public int getLogicalSize() {
		return this.size - deletedRecords.getCardinality();
	}

	/** Gets the set of records that verify a logical expression.
	 * @param logicalExpr a logical expression.
	 * <br>Supported operators depends on the {@link Evaluator} built by the {@link TagsTableFactory} used to create this table.
	 * @param failIfUnknown true if the method should fail if a tag is unknown, false if unknown tags should be added automatically.
	 * @return a bitmap. Each set index in the bitmap is the index of a record that satisfies the logical expression 
	 * @throws UnknownTagException if the expression refers to an unknown tag and <i>failIfUnknown</i> is true. Otherwise unknown tags are considered false.
	 */
	public Bitmap evaluate(String logicalExpr, boolean failIfUnknown) {
		Bitmap bitmap = evaluator.evaluate(logicalExpr, failIfUnknown);
		if (logicalSize!=size) {
			// If bitmap is not locked, but the table is, it means the bitmap was create by the evaluator itself
			// So, it is not useful to clone it.
			if (bitmap.isLocked() || !isLocked()) {
				bitmap = bitmap.clone();
			}
			bitmap.andNot(deletedRecords);
		}
		return bitmap.getLocked();
	}
	
	/** Gets the set of records having a tag.
	 * @param tag The tag
	 * @return a record set.
	 * <br><b>Warning:</b> There are side effects between the returned instance and the table.
	 */
	public Bitmap getBitMapIndex(T tag) {
		return this.tagToBitmap.get(tag);
	}
	
	/** Clones the table.
	 * This method performs a deep clone and guarantees no side effect between this and the returned table.
	 * @return a new Table that contains a modifiable copy of this.
	 */
	@Override
	public TagsTable<T> clone() {
		try {
			@SuppressWarnings("unchecked")
			TagsTable<T> result = (TagsTable<T>) super.clone();
			result.tagToBitmap = factory.buildmap();
			for (T key : this.tagToBitmap.keySet()) {
				IntIterator iterator = getBitMapIndex(key).getIterator();
				Bitmap freshBitmap = factory.create();
				while (iterator.hasNext()) {
					freshBitmap.add(iterator.next());
				}
				freshBitmap.trim();
				result.tagToBitmap.put(key, freshBitmap);
			}
			result.deletedRecords = deletedRecords.clone();
			result.evaluator = factory.buildEvaluator(result);
			result.isLocked = false;
			return result;
		} catch (CloneNotSupportedException e) {
			throw new UnexpectedCloneNotSupportedException(e);
		}
	}
	
	/** Gets an immutable copy of a table.
	 * @return a new table. This method guarantees no side effect between this and the returned table.
	 */
	public TagsTable<T> getLocked() {
		if (isLocked) {
			return this;
		} else {
			TagsTable<T> result = (TagsTable<T>) clone();
			result.tagToBitmap = factory.buildmap();
			for (T key : tagToBitmap.keySet()) {
				result.tagToBitmap.put(key, tagToBitmap.get(key).getLocked());
			}
			result.deletedRecords = deletedRecords.clone();
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

	/** Gets the factory used to build this table.
	 * @return a TagsTableFactory
	 */
	public TagsTableFactory<T> getFactory() {
		return factory;
	}

	/** Gets the tags associated with an id.
	 * @param id A record id
	 * @return an iterator on tags presents in this record.
	 * @throws IllegalArgumentException if id is greater than or equal to {@link #getSize()}.
	 */
	public Iterator<T> getTags(int id) {
		if (id>=getSize()) {
			throw new IllegalArgumentException();
		}
		return new TagsIterator(id);
	}
	
	/** Tests whether a record contains a tag. 
	 * @param id A record id.
	 * @param tag A tag
	 * @return true if the record contains the tag
	 */
	public boolean contains(int id, T tag) {
		Bitmap bitmap = tagToBitmap.get(tag);
		return bitmap==null?false:bitmap.contains(id);
	}

	/** Adds a tag to a record. 
	 * @param id A record id.
	 * @param tag A tag
	 * @param failIfUnknown true if the method should fail if a tag is unknown, false if unknown tags should be added automatically.
	 * @throws UnknownTagException if a tag is unknown and <i>failIfUnknown</i> is true.
	 * @throws IllegalStateException if this is locked
	 */
	public void add(int id, T tag, boolean failIfUnknown) {
		check();
		Bitmap bitmap = tagToBitmap.get(tag);
		if (bitmap==null) {
			if (failIfUnknown) {
				throw new UnknownTagException(tag.toString());
			} else {
				bitmap = factory.create();
				tagToBitmap.put(tag, bitmap);
			}
		}
		bitmap.add(id);
	}

	/** Removes a tag from a record.
	 * <br>If the record not contains the tag, this method does nothing.
	 * @param id A record id.
	 * @param tag A tag
	 * @throws IllegalStateException if this is locked
	 */
	public void remove(int id, T tag) {
		check();
		Bitmap bitmap = tagToBitmap.get(tag);
		if (bitmap!=null) {
			bitmap.remove(id);
		}
	}
	
	@Override
	public String toString() {
		//TODO Limit the String size
		StringBuilder builder = new StringBuilder();
		builder.append(deletedRecords.toString());
		for (T key:tagToBitmap.keySet()) {
			builder.append('\n');
			builder.append(key.toString());
			builder.append(':');
			builder.append(tagToBitmap.get(key).toString());
		}
		return builder.toString();
	}
}
