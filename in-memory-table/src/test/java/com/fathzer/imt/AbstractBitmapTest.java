package com.fathzer.imt;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractBitmapTest {
	private TagsTableFactory<? extends Object> factory;
	private Bitmap locked;
	
	@Before
	public void setUp() {
		factory = buildFactory();
		locked = factory.create();
		locked.add(10);
		locked = locked.getLocked();
	}

	protected abstract TagsTableFactory<? extends Object> buildFactory();

	@Test
	public void doTest() {
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
		assertEquals(1, b.getCardinality());
	}
	
	@Test (expected=IllegalStateException.class)
	public void doTestLockAdd() {
		locked.add(5);
	}
	
	@Test (expected=IllegalStateException.class)
	public void doTestLockRemove() {
		locked.remove(5);
	}
	
	@Test (expected=IllegalStateException.class)
	public void doTestLockNot() {
		locked.not(6);
	}
	
	@Test (expected=IllegalStateException.class)
	public void doTestLockAnd() {
		locked.and(factory.create());
	}
	
	@Test (expected=IllegalStateException.class)
	public void doTestLockAndNot() {
		locked.andNot(factory.create());
	}
	
	@Test (expected=IllegalStateException.class)
	public void doTestLockOr() {
		locked.or(factory.create());
	}
	
	@Test (expected=IllegalStateException.class)
	public void doTestLockXOr() {
		locked.xor(factory.create());
	}
	
	@Test
	public void testLocked() {
		assertTrue(locked.isLocked());
		locked.clone().add(3);
	}
}
