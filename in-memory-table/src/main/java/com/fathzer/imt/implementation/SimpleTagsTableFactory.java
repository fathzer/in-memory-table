package com.fathzer.imt.implementation;

import java.io.Serializable;
import java.util.HashMap;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.BitmapMap;
import com.fathzer.imt.Evaluator;
import com.fathzer.imt.TagsTableFactory;

/** A simple abstract factory that uses a HashMap and {@link DefaultEvaluator}.
 */
public abstract class SimpleTagsTableFactory implements TagsTableFactory<String>, Serializable {
	private static final long serialVersionUID = 1L;

	/** Factory that uses Roaring bitmaps.
	 */
	public static final SimpleTagsTableFactory ROARING_FACTORY = new SimpleTagsTableFactory() {
		private static final long serialVersionUID = 1L;

		@Override
		public Bitmap create() {
			return new RoaringBitmap();
		}
	};
	/** Factory that uses EWAH compressed bitmaps.
	 */
	public static final SimpleTagsTableFactory EWAH_FACTORY = new SimpleTagsTableFactory() {
		private static final long serialVersionUID = 1L;

		@Override
		public Bitmap create() {
			return new EWAHBitmap();
		}
	};
	/** Factory that uses simple java.util.BitSet.
	 */
	public static final SimpleTagsTableFactory BITSET_FACTORY = new SimpleTagsTableFactory() {
		private static final long serialVersionUID = 1L;

		@Override
		public Bitmap create() {
			return new BitSetBitmap();
		}
	};
	
	/** Constructor. */
	protected SimpleTagsTableFactory() {
	}

	@Override
	public Evaluator<String> getEvaluator() {
		return new ThreadSafeEvaluator<String>() {
			@Override
			protected Evaluator<String> buildUnsafeEvaluator() {
				return new DefaultEvaluator<String>() {
					@Override
					protected String stringToTag(String string) {
						return string;
					}
				};
			}
		};
	}

	@Override
	public BitmapMap<String> buildmap() {
		return new DefaultBitmapMap<String>();
	}

	private static class DefaultBitmapMap<K> extends HashMap<K, Bitmap> implements BitmapMap<K> {
		private static final long serialVersionUID = 1L;
	}

	@Override
	public abstract Bitmap create();
}
