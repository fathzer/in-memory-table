package com.fathzer.imt.implementation;

import java.util.Iterator;

import com.fathzer.imt.BitmapAdapter;
import com.fathzer.imt.util.IntIterator;
import com.googlecode.javaewah.EWAHCompressedBitmap;

public final class EWAH64BitmapAdapter implements BitmapAdapter<EWAHCompressedBitmap> {
	@Override
	public EWAHCompressedBitmap and(EWAHCompressedBitmap b1, EWAHCompressedBitmap b2) {
		return b1.and(b2);
	}

	@Override
	public EWAHCompressedBitmap or(EWAHCompressedBitmap b1, EWAHCompressedBitmap b2) {
		return b1.or(b2);
	}

	@Override
	public EWAHCompressedBitmap or(Iterator<EWAHCompressedBitmap> bitmaps) {
		return com.googlecode.javaewah.FastAggregation.or(bitmaps);
	}

	@Override
	public EWAHCompressedBitmap not(EWAHCompressedBitmap bitmap, int size) {
		try {
			EWAHCompressedBitmap result = bitmap.clone();
			result.setSizeInBits(size, false);
			result.not();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	@Override
	public boolean contains(EWAHCompressedBitmap bitmap, int index) {
		return bitmap.get(index);
	}

	@Override
	public boolean isEmpty(EWAHCompressedBitmap bitmap) {
		return bitmap.isEmpty();
	}

	@Override
	public long getSizeInBytes(EWAHCompressedBitmap bitmap) {
		return bitmap.sizeInBytes();
	}

	@Override
	public IntIterator getIterator(EWAHCompressedBitmap bitmap) {
		final com.googlecode.javaewah.IntIterator iter = bitmap.intIterator();
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
	public EWAHCompressedBitmap create() {
		return new EWAHCompressedBitmap();
	}

	@Override
	public EWAHCompressedBitmap create(IntIterator iter) {
		EWAHCompressedBitmap bm = new EWAHCompressedBitmap();
		while (iter.hasNext()) {
			bm.set(iter.next());
		}
		bm.trim();
		return bm;
	}

	@Override
	public int getCardinality(EWAHCompressedBitmap bitmap) {
		return bitmap.cardinality();
	}

	@Override
	public void add(EWAHCompressedBitmap bitmap, int index) {
		bitmap.set(index);
	}

	@Override
	public void remove(EWAHCompressedBitmap bitmap, int index) {
		bitmap.clear(index);
	}
}