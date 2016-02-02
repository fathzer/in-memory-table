package com.fathzer.imt.implementation;

import java.io.Serializable;

import com.fathzer.imt.Bitmap;
import com.fathzer.imt.util.IntIterator;
import com.fathzer.imt.util.UnexpectedCloneNotSupportedException;
import com.googlecode.javaewah.EWAHCompressedBitmap;

/** A Bitmap backed by the excellent <a href="https://github.com/lemire/javaewah">javaewah library from D. Lemire</a>. 
 * @author Jean-Marc Astesana
 */
public class EWAHBitmap implements Bitmap, Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	private EWAHCompressedBitmap set;
	private boolean isLocked;
	
	/** Constructor.
	 * <br>Builds a new empty bitmap. 
	 */
	public EWAHBitmap() {
		this.set = new EWAHCompressedBitmap();
		this.isLocked = false;
	}

	@Override
	public int getCardinality() {
		return set.cardinality();
	}
	
	@Override
	public void or(Bitmap bitmap) {
		check();
		set = set.or(((EWAHBitmap)bitmap).set);
	}

	@Override
	public void xor(Bitmap bitmap) {
		check();
		set = set.xor(((EWAHBitmap)bitmap).set);
	}

	@Override
	public void and(Bitmap bitmap) {
		check();
		set = set.and(((EWAHBitmap)bitmap).set);
	}

	@Override
	public void andNot(Bitmap bitmap) {
		check();
		set = set.andNot(((EWAHBitmap)bitmap).set);
	}

	@Override
	public void not(int size) {
		check();
		this.set.setSizeInBits(size, false);
		this.set.not();
	}

	@Override
	public void trim() {
		check();
		set.trim();
	}
	
	@Override
	public IntIterator getIterator() {
		final com.googlecode.javaewah.IntIterator iter = set.intIterator();
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
		return set.sizeInBytes();
	}

	@Override
	public Bitmap getLocked() {
		if (isLocked) {
			return this;
		} else {
			EWAHBitmap result = (EWAHBitmap) clone();
			try {
				result.set = set.clone();
				result.set.trim();
				result.isLocked = true;
				return result;
			} catch (CloneNotSupportedException e) {
				throw new UnexpectedCloneNotSupportedException(e);
			}
		}
	}
	
	/** Clones this bitmap.
	 * @return A <b>unlocked</b> copy of this bitmap 
	 */
	@Override
	public EWAHBitmap clone() {
		try {
			EWAHBitmap result = (EWAHBitmap) super.clone();
			result.isLocked = false;
			result.set = this.set.clone();
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
}
