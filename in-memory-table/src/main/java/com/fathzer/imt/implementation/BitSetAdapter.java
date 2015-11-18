package com.fathzer.imt.implementation;

import java.util.BitSet;
import java.util.Iterator;

import com.fathzer.imt.BitmapAdapter;
import com.fathzer.imt.util.IntIterator;

public final class BitSetAdapter implements BitmapAdapter<BitSet> {
	@Override
	public BitSet and(BitSet b1, BitSet b2) {
		BitSet result = (BitSet) b1.clone();
		result.and(b2);
		return result;
	}

	@Override
	public BitSet or(BitSet b1, BitSet b2) {
		BitSet result = (BitSet) b1.clone();
		result.or(b2);
		return result;
	}

	@Override
	public BitSet or(Iterator<BitSet> bitmaps) {
		BitSet bitmap = new BitSet();
		while (bitmaps.hasNext()) {
			bitmap.or(bitmaps.next());
		}
		return bitmap;
	}

	@Override
	public BitSet not(BitSet bitmap, int size) {
  	BitSet result = (BitSet) bitmap.clone();
    result.flip(0, size);
    return result;
	}

	@Override
	public boolean contains(BitSet bitmap, int index) {
		return bitmap.get(index);
	}

	@Override
	public boolean isEmpty(BitSet bitmap) {
		return bitmap.isEmpty();
	}

	@Override
	public long getSizeInBytes(BitSet bitmap) {
		//FIXME 1 bit requires more than 0 bytes!
		return bitmap.length()/8;
	}

	@Override
	public IntIterator getIterator(BitSet bitmap) {
		return new BitSetIterator(bitmap);
	}

	@Override
	public BitSet create() {
    return new BitSet();
	}

	@Override
	public BitSet create(IntIterator iter) {
		BitSet result = new BitSet();
		while (iter.hasNext()) {
			result.set(iter.next());
		}
		return result;
	}

	@Override
	public int getCardinality(BitSet bitmap) {
		return bitmap.cardinality();
	}

	@Override
	public void add(BitSet bitmap, int index) {
		bitmap.set(index);
	}

	@Override
	public void remove(BitSet bitmap, int index) {
		bitmap.clear(index);
	}
}