package com.fathzer.imt.implementation;

import java.util.Iterator;

import org.roaringbitmap.FastAggregation;
import org.roaringbitmap.RoaringBitmap;

import com.fathzer.imt.BitmapAdapter;
import com.fathzer.imt.util.IntIterator;

public final class RoaringBitmapAdapter implements BitmapAdapter<RoaringBitmap> {
	@Override
	public RoaringBitmap and(RoaringBitmap b1, RoaringBitmap b2) {
		return RoaringBitmap.and(b1,b2);
	}

	@Override
	public RoaringBitmap or(RoaringBitmap b1, RoaringBitmap b2) {
		return RoaringBitmap.or(b1,b2);
	}

	@Override
	public RoaringBitmap or(Iterator<RoaringBitmap> bitmaps) {
		return FastAggregation.priorityqueue_or(bitmaps);
	}

	@Override
	public RoaringBitmap not(RoaringBitmap bitmap, int size) {
  	return RoaringBitmap.flip(bitmap, 0, size);
	}

	@Override
	public boolean contains(RoaringBitmap bitmap, int index) {
		return bitmap.contains(index);
	}

	@Override
	public boolean isEmpty(RoaringBitmap bitmap) {
		return bitmap.isEmpty();
	}

	@Override
	public long getSizeInBytes(RoaringBitmap bitmap) {
		return bitmap.getSizeInBytes();
	}

	@Override
	public IntIterator getIterator(RoaringBitmap bitmap) {
		final org.roaringbitmap.IntIterator iter = bitmap.getIntIterator();
		return new IntIterator() {
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public int next() {
				return iter.next();
			}
		};
	}

	@Override
	public RoaringBitmap create() {
		return new RoaringBitmap();
	}

	@Override
	public RoaringBitmap create(IntIterator iter) {
		RoaringBitmap bitmap = new RoaringBitmap();
		while (iter.hasNext()) {
			bitmap.add(iter.next());
		}
		bitmap.trim();
		return bitmap;
	}

	@Override
	public int getCardinality(RoaringBitmap bitmap) {
		return bitmap.getCardinality();
	}

	@Override
	public void add(RoaringBitmap bitmap, int index) {
		bitmap.add(index);
	}

	@Override
	public void remove(RoaringBitmap bitmap, int index) {
		bitmap.remove(index);
	}
}