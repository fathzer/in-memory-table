package com.fathzer.imt.implementation;

import java.io.Serializable;
import java.util.BitSet;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.util.IntIterator;
import com.fathzer.imt.util.UnexpectedCloneNotSupportedException;

/** A Bitmap backed by the java.util.BitSet class. 
 * @author Jean-Marc Astesana
 */
public class BitSetBitmap implements Bitmap, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private BitSet set;
	private boolean isLocked;
	
	/** Constructor.
	 * <br>Builds a new empty bitmap. 
	 */
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
	public void xor(Bitmap bitmap) {
		check();
		set.xor(((BitSetBitmap)bitmap).set);
	}

	@Override
	public void and(Bitmap bitmap) {
		check();
		set.and(((BitSetBitmap)bitmap).set);
	}

	@Override
	public void andNot(Bitmap bitmap) {
		check();
		set.andNot(((BitSetBitmap)bitmap).set);
	}

	@Override
	public void not(int size) {
		check();
		set.flip(0, size);
	}

	@Override
	public boolean intersects(Bitmap bitmap) {
		return set.intersects(((BitSetBitmap)bitmap).set);
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
		int len = set.length();
		int result = len/64+1;
		if (len % 64 == 0) {
			result--;
		}
		return 8*result;
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
	
	/** Clones this bitmap.
	 * @return A <b>unlocked</b> copy of this bitmap 
	 */
	@Override
	public BitSetBitmap clone() {
		try {
			BitSetBitmap result = (BitSetBitmap) super.clone();
			result.isLocked = false;
			result.set = (BitSet) this.set.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new UnexpectedCloneNotSupportedException(e);
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
	
	@Override
	public String toString() {
		return set.toString();
	}
}
