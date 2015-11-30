package com.fathzer.imt.implementation;

import java.util.HashMap;
import java.util.Iterator;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.BitmapMap;
import com.fathzer.imt.TagsTable;
import com.fathzer.imt.TagsTableFactory;
import com.fathzer.soft.javaluator.AbstractEvaluator;

/** A simple factory that uses a Hashmap and default evaluator.
 * @param <V> The bitmap type.
 */
public abstract class SimpleTagsTableFactory<V extends Bitmap> implements TagsTableFactory<String, V> {
	public static final SimpleTagsTableFactory<RoaringBitmap> ROARING_FACTORY = new SimpleTagsTableFactory<RoaringBitmap>() {
		@Override
		public RoaringBitmap create() {
			return new RoaringBitmap();
		}
	};
	public static final SimpleTagsTableFactory<BitSetBitmap> BITSET_FACTORY = new SimpleTagsTableFactory<BitSetBitmap>() {
		@Override
		public BitSetBitmap create() {
			return new BitSetBitmap();
		}
	};
	
	protected SimpleTagsTableFactory() {
	}

	@Override
	public AbstractEvaluator<V> buildEvaluator(TagsTable<String, V> table) {
		return new DefaultEvaluator<String, V>(table, this);
	}

	@Override
	public BitmapMap<String, V> buildmap() {
		return new DefaultBitmapMap<String, V>();
	}

	@Override
	public String stringToTag(String string) {
		return string;
	}
	
	private static class DefaultBitmapMap<K,V> extends HashMap<K, V> implements BitmapMap<K, V> {
		private static final long serialVersionUID = 1L;
	}

	@Override
	public abstract V create();

	@Override
	public V or(Iterator<V> bitmaps) {
		V result = create();
		while (bitmaps.hasNext()) {
			result.or(bitmaps.next());
		}
		return result;
	}
}
