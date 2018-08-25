package com.fathzer.imt.implementation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.roaringbitmap.RoaringBitmap;

public class RoaringBugTest {
	@Test
	public void test() {
		RoaringBitmap r1 = new RoaringBitmap();
		// Strange thing: Replace this line by r1.add(131000) and the bug vanishes!
		r1.flip(131000, 131001);
		RoaringBitmap r2 = new RoaringBitmap();
		r2.add(220000);
		RoaringBitmap r3 = new RoaringBitmap();
		int killingPosition = 66000;
		r3.add(killingPosition);
		
		assertFalse(r1.contains(killingPosition)); //ok
		r2.or(r1);
		assertFalse(r1.contains(killingPosition)); //ok
		r2.or(r3);
		assertFalse(r1.contains(killingPosition)); //ko
	}
}
