package com.fathzer.imt.implementation;

import java.util.Iterator;

import com.fathzer.imt.BitmapAdapter;
import com.fathzer.imt.util.IntIterator;
import com.googlecode.javaewah32.EWAHCompressedBitmap32;
import com.googlecode.javaewah32.FastAggregation32;

public final class EWAH32BitMapAdapter implements BitmapAdapter<EWAHCompressedBitmap32> {
	@Override
	public EWAHCompressedBitmap32 and(EWAHCompressedBitmap32 b1, EWAHCompressedBitmap32 b2) {
		return b1.and(b2);
	}

	@Override
	public EWAHCompressedBitmap32 or(EWAHCompressedBitmap32 b1, EWAHCompressedBitmap32 b2) {
		return b1.or(b2);
	}

	@Override
	public EWAHCompressedBitmap32 or(Iterator<EWAHCompressedBitmap32> bitmaps) {
		return FastAggregation32.or(bitmaps);
	}

	@Override
	public EWAHCompressedBitmap32 not(EWAHCompressedBitmap32 bitmap, int size) {
		try {
			EWAHCompressedBitmap32 result = bitmap.clone();
			result.setSizeInBits(size, false);
			result.not();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	@Override
	public boolean contains(EWAHCompressedBitmap32 bitmap, int index) {
		return bitmap.get(index);
	}

	@Override
	public boolean isEmpty(EWAHCompressedBitmap32 bitmap) {
		return bitmap.isEmpty();
	}

	@Override
	public long getSizeInBytes(EWAHCompressedBitmap32 bitmap) {
		return bitmap.sizeInBytes();
	}

	@Override
	public IntIterator getIterator(EWAHCompressedBitmap32 bitmap) {
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
	public EWAHCompressedBitmap32 create() {
		return new EWAHCompressedBitmap32();
	}

	@Override
	public EWAHCompressedBitmap32 create(IntIterator iter) {
		EWAHCompressedBitmap32 bitmap = new EWAHCompressedBitmap32();
		while (iter.hasNext()) {
			Integer pos = iter.next();
			bitmap.set(pos);
		}
		bitmap.trim();
		return bitmap;
	}

	@Override
	public int getCardinality(EWAHCompressedBitmap32 bitmap) {
		return bitmap.cardinality();
	}

	@Override
	public void add(EWAHCompressedBitmap32 bitmap, int index) {
		bitmap.set(index);
	}

	@Override
	public void remove(EWAHCompressedBitmap32 bitmap, int index) {
		bitmap.clear(index);
	}
}