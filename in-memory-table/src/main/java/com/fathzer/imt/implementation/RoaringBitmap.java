package com.fathzer.imt.implementation;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.util.IntIterator;

public class RoaringBitmap implements Bitmap, Cloneable {
	private org.roaringbitmap.RoaringBitmap set;
	private boolean isLocked;
	
	public RoaringBitmap() {
		set = new org.roaringbitmap.RoaringBitmap();
		this.isLocked = false;
	}

	@Override
	public int getCardinality() {
		return set.getCardinality();
	}
	@Override
	public void or(Bitmap bitmap) {
		check();
		set.or(((RoaringBitmap)bitmap).set);
	}

	@Override
	public void and(Bitmap bitmap) {
		check();
		set.and(((RoaringBitmap)bitmap).set);
	}

	@Override
	public void not(int size) {
		check();
	    set.flip(0, size);
	}

	@Override
	public void trim() {
		check();
		set.trim();
	}
	
	@Override
	public IntIterator getIterator() {
		final org.roaringbitmap.IntIterator iter = set.getIntIterator();
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
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public boolean contains(int index) {
		return set.contains(index);
	}

	@Override
	public void add(int index) {
		check();
		set.add(index);
	}

	@Override
	public void remove(int index) {
		check();
		set.remove(index);
	}

	@Override
	public long getSizeInBytes() {
		return set.getSizeInBytes();
	}

	@Override
	public Bitmap getLocked() {
		if (isLocked) {
			return this;
		} else {
			RoaringBitmap result = (RoaringBitmap) clone();
			result.isLocked = true;
			result.set = set.clone();
			return result;
		}
	}
	
	@Override
	public RoaringBitmap clone() {
		try {
			RoaringBitmap result = (RoaringBitmap) super.clone();
			result.isLocked = false;
			result.set = this.set.clone();
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
