package com.fathzer.imt.implementation;

import java.util.BitSet;
import java.util.NoSuchElementException;

import com.fathzer.imt.util.IntIterator;

public class BitSetIterator implements IntIterator {
	private int next = -1;
	private BitSet bitset;
	
	public BitSetIterator(BitSet bitset) {
		this.bitset = bitset;
		this.next = bitset.nextSetBit(0);
	}

	@Override
	public boolean hasNext() {
		return this.next>=0;
	}

	@Override
	public int next() {
		if (this.next<0) {
			throw new NoSuchElementException();
		}
		int result = this.next;
		this.next = bitset.nextSetBit(next+1);
		return result;
	}
}
