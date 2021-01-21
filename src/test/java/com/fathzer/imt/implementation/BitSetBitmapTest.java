package com.fathzer.imt.implementation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fathzer.imt.AbstractBitmapTest;
import com.fathzer.imt.TagsTableFactory;

public class BitSetBitmapTest extends AbstractBitmapTest {
	@Override
	protected TagsTableFactory<? extends Object> buildFactory() {
		return SimpleTagsTableFactory.BITSET_FACTORY;
	}
	
	@Test
	public void testSize() {
		BitSetBitmap b = new BitSetBitmap();
		assertEquals(0, b.getSizeInBytes());
		b.add(1);
		assertEquals(8, b.getSizeInBytes());
		b.add(63);
		assertEquals(8, b.getSizeInBytes());
		b.add(64);
		assertEquals(16, b.getSizeInBytes());
	}
}
