package com.fathzer.imt;

import static org.junit.Assert.*;

import org.junit.Test;

import shaded.org.apache.commons.codec.net.BCodec;

import com.fathzer.imt.implementation.SimpleTagsTableFactory;

public class BitmapTest {

	@Test
	public void test() {
		doTest (SimpleTagsTableFactory.BITSET_FACTORY);
		doTest (SimpleTagsTableFactory.ROARING_FACTORY);
		doTest (SimpleTagsTableFactory.EWAH_FACTORY);
	}

	private void doTest(TagsTableFactory factory) {
		Bitmap b = factory.create();
		b.add(5);
		assertEquals(1, b.getCardinality());
		assertTrue(b.contains(5));
		assertFalse(b.contains(6));
		
		Bitmap bc = b.clone();
		assertEquals(b.getCardinality(), bc.getCardinality());
		bc.not(7);
		assertEquals(6, bc.getCardinality());
		
		bc.and(b);
		assertEquals(0, bc.getCardinality());
		
		b.add(1);
		assertEquals(2, b.getCardinality());
		b.remove(5);
		assertEquals(1, b.getCardinality());
		
		bc.add(2);
		bc.or(b);
		assertEquals(2, bc.getCardinality());
		b.xor(bc);
		assertEquals(5, bc.getCardinality());
	}

}
