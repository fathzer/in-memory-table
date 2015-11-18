package com.fathzer.imt;

import java.util.Iterator;
import java.util.NoSuchElementException;

class Record implements Iterator<String> {
	private String content;
	private int current;
	private int nextIndex;
	
	public Record(String mtc) {
		this.content = mtc;
		this.current = 0;
		if (!mtc.isEmpty()) {
			this.nextIndex = mtc.indexOf('/');
		} else {
			this.current = -1;
		}
	}
	
	@Override
	public boolean hasNext() {
		return this.current >= 0;
	}

	@Override
	public String next() {
		if (this.current<0) {
			throw new NoSuchElementException();
		} else if (nextIndex>=0) {
			String result = content.substring(current, nextIndex);
			current = nextIndex+1;
			nextIndex = content.indexOf('/', current);
			return result;
		} else {
			String result = content.substring(current, content.length());
			current = nextIndex;
			return result;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}