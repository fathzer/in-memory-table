package com.fathzer.imt.implementation;

import java.util.BitSet;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.util.IntIterator;

public class BitSetBitmap implements Bitmap, Cloneable {
	private BitSet set;
	private boolean isLocked;
	
	public BitSetBitmap() {
		this.set = new BitSet();
		this.isLocked = false;
	}

	@Override
	public int getCardinality() {
		return set.cardinality();
	}
	@Override
	public void or(Bitmap bitmap) {
		check();
		set.or(((BitSetBitmap)bitmap).set);
	}

	@Override
	public void and(Bitmap bitmap) {
		check();
		set.and(((BitSetBitmap)bitmap).set);
	}

	@Override
	public void not(int size) {
		check();
	    set.flip(0, size);
	}

	@Override
	public void trim() {
		// Nothing to do
	}
	
	@Override
	public IntIterator getIterator() {
		return new BitSetIterator(set);
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(int index) {
		return set.get(index);
	}

	@Override
	public void add(int index) {
		check();
		set.set(index);
	}

	@Override
	public void remove(int index) {
		check();
		set.clear(index);
	}

	@Override
	public long getSizeInBytes() {
		//FIXME 1 bit requires more than 0 bytes!
		return set.length()/8;
	}

	@Override
	public Bitmap getLocked() {
		if (isLocked) {
			return this;
		} else {
			BitSetBitmap result;
			result = (BitSetBitmap) clone();
			result.isLocked = true;
			return result;
		}
	}
	
	public BitSetBitmap clone() {
		try {
			BitSetBitmap result = (BitSetBitmap) super.clone();
			result.isLocked = false;
			result.set = (BitSet) this.set.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isLocked() {
		return isLocked;
	}


	private void check() {
		if (isLocked) {
			throw new IllegalStateException();
		}
	}
}
