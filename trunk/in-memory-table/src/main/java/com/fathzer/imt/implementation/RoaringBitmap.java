package com.fathzer.imt.implementation;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.util.IntIterator;

public class RoaringBitmap implements Bitmap, Cloneable {
	public org.roaringbitmap.RoaringBitmap set;
	private boolean isLocked;
	
	public RoaringBitmap() {
		this(new org.roaringbitmap.RoaringBitmap());
	}
	
	private RoaringBitmap(org.roaringbitmap.RoaringBitmap set) {
		this.set = set;
		this.isLocked = false;
	}

	@Override
	public int getCardinality() {
		return set.getCardinality();
	}
	
	@Override
	public void or(Bitmap bitmap) {
		check();
		this.set.or(((RoaringBitmap)bitmap).set);
	}

	@Override
	public void xor(Bitmap bitmap) {
		check();
		set.xor(((RoaringBitmap)bitmap).set);
	}

	@Override
	public void and(Bitmap bitmap) {
		check();
		this.set.and(((RoaringBitmap)bitmap).set);
	}

	@Override
	public void andNot(Bitmap bitmap) {
		check();
		set.andNot(((RoaringBitmap)bitmap).set);
	}

	@Override
	public void not(int size) {
		check();
		this.set.flip(0, size);
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
			result.set.trim();
			result.isLocked = true;
			return result;
		}
	}
	
	@Override
	public RoaringBitmap clone() {
		return new RoaringBitmap(set.clone());
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
