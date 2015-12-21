package com.fathzer.imt.implementation;

import java.util.HashMap;
import java.util.Iterator;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.BitmapMap;
import com.fathzer.imt.TagsTable;
import com.fathzer.imt.TagsTableFactory;
import com.fathzer.soft.javaluator.AbstractEvaluator;

/** A simple factory that uses a HashMap and default evaluator.
 */
public abstract class SimpleTagsTableFactory implements TagsTableFactory<String> {
	public static final SimpleTagsTableFactory ROARING_FACTORY = new SimpleTagsTableFactory() {
		@Override
		public Bitmap create() {
			return new RoaringBitmap();
		}
	};
	public static final SimpleTagsTableFactory BITSET_FACTORY = new SimpleTagsTableFactory() {
		@Override
		public Bitmap create() {
			return new BitSetBitmap();
		}
	};
	
	protected SimpleTagsTableFactory() {
	}

	@Override
	public AbstractEvaluator<Bitmap> buildEvaluator(TagsTable<String> table) {
		return new DefaultEvaluator<String>(table);
	}

	@Override
	public BitmapMap<String> buildmap() {
		return new DefaultBitmapMap<String>();
	}

	@Override
	public String stringToTag(String string) {
		return string;
	}
	
	private static class DefaultBitmapMap<K> extends HashMap<K, Bitmap> implements BitmapMap<K> {
		private static final long serialVersionUID = 1L;
	}

	@Override
	public abstract Bitmap create();

	@Override
	public Bitmap or(Iterator<Bitmap> bitmaps) {
		Bitmap result = create();
		while (bitmaps.hasNext()) {
			result.or(bitmaps.next());
		}
		return result;
	}
}
