package com.fathzer.imt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.Test;

import static org.junit.Assert.*;

import com.fathzer.imt.implementation.SimpleTagsTableFactory;
import com.fathzer.imt.util.IntIterator;

public class TableTest {

	@Test
	public void test() {
		doTest (SimpleTagsTableFactory.BITSET_FACTORY);
		doTest (SimpleTagsTableFactory.ROARING_FACTORY);
		doTest (SimpleTagsTableFactory.EWAH_FACTORY);
	}

	private void doTest(SimpleTagsTableFactory bitsetFactory) {
		TagsTable<String> table = new TagsTable<>(bitsetFactory);
		int index = table.addRecord(new Record("A/C/E"), false);
		assertEquals(0, index);
		List<String> tags = IteratorUtils.toList(table.getTags(index));
		assertEquals(3,tags.size());
		Collections.sort(tags);
		assertEquals("A", tags.get(0));
		assertEquals("C", tags.get(1));
		assertEquals("E", tags.get(2));
		
		assertEquals(1, table.addRecord(new Record("B/D/F"), false));
		Bitmap recordSet = table.evaluate("A && C", true);
		assertEquals(1, recordSet.getCardinality());
		IntIterator iterator = recordSet.getIterator();
		assertEquals(0, iterator.next());
		
		assertEquals(0, table.evaluate("A && B", true).getCardinality());
		
		recordSet = table.evaluate("A || D", true);
		iterator = recordSet.getIterator();
		assertEquals(0, iterator.next());
		assertEquals(1, iterator.next());
		assertEquals(2, recordSet.getCardinality());
		
		recordSet = table.evaluate("!E && A||D", true);
		iterator = recordSet.getIterator();
		assertEquals(1, iterator.next());
		assertEquals(1, recordSet.getCardinality());
		
		recordSet = table.evaluate("!E", true);
		assertEquals(1, recordSet.getCardinality());
		iterator = recordSet.getIterator();
		assertEquals(1, iterator.next());
		
		// Test constants
		final String TRUE = "ALWAYS_TRUE";
		final String FALSE = "ALWAYS_FALSE"; 
		assertEquals(0, table.evaluate(FALSE, true).getCardinality());
		assertEquals(2, table.evaluate(TRUE, true).getCardinality());
		
		// Test unknown variables are false
		recordSet = table.evaluate("Z", false);
		assertEquals(0, recordSet.getCardinality());
		
		// Test remove/add
		table.remove(0, "A");
		assertEquals(2, table.getSize());
		assertEquals(2, table.getLogicalSize());
		assertEquals(0, table.evaluate("A", true).getCardinality());
		assertFalse(table.contains(0, "A"));
		table.add(0, "A", false);
		assertEquals(2, table.getLogicalSize());
		assertEquals(1, table.evaluate("A", true).getCardinality());
		assertTrue(table.contains(0, "A"));
		
		// Test delete
		table.deleteRecord(0);
		assertEquals(1, table.getLogicalSize());
		assertEquals(0, table.evaluate("C", true).getCardinality());
		assertEquals(1, table.evaluate(TRUE, true).getCardinality());
		
		// Test add again and lock, unlock
		table = table.getLocked();
		assertTrue(table.isLocked());
		table = (TagsTable<String>) table.clone();
		assertFalse(table.isLocked());
		table.addRecord(new Record("A/C/E"), false);
		assertEquals(2, table.getLogicalSize());
		assertEquals(1, table.evaluate("C", true).getCardinality());
		
		// Test add unknown
		table.add(0, "ZZ", false);
		assertTrue(table.contains(0, "ZZ"));
	}
	
	@Test
	public void emptyExpressionTest() {
		TagsTable<String> table = new TagsTable<>(SimpleTagsTableFactory.BITSET_FACTORY);
		int index = table.addRecord(new Record("A/C/E"), false);
		table.addRecord(new Record("B/D/F"), false);
		table.deleteRecord(index);

		// Test empty logical expression
		assertEquals(table.getLogicalSize(), table.evaluate("", false).getCardinality());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSerialization() throws Exception {
		TagsTable<String> table = new TagsTable<>(SimpleTagsTableFactory.BITSET_FACTORY);
		int index = table.addRecord(new Record("A/C/E"), false);
		table.addRecord(new Record("B/D/F"), false);
		table.deleteRecord(index);

		// Test empty logical expression
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ObjectOutputStream oo = new ObjectOutputStream(out)) {
			oo.writeObject(table);
		}
		byte[] bytes = out.toByteArray();
		System.out.println ("Just for fun: size="+bytes.length+"bytes");
		try (ObjectInputStream oo = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
			table = (TagsTable<String>) oo.readObject();
		}
		assertEquals(table.getLogicalSize(), table.evaluate("", false).getCardinality());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testLocked() {
		doLocked(SimpleTagsTableFactory.BITSET_FACTORY);
	}
	
	private void doLocked(SimpleTagsTableFactory factory) {
		TagsTable<String> table = new TagsTable<String>(factory);
		table.addRecord(new Record("A/C/E"), false);
		table = table.getLocked();
		table.remove(0, "A");;
	}

	@Test(expected = UnknownTagException.class)
	public void doAddUnknown() {
		doAddUnknown(SimpleTagsTableFactory.BITSET_FACTORY);
	}
	
	private void doAddUnknown(SimpleTagsTableFactory factory) {
		TagsTable<String> table = new TagsTable<String>(factory);
		table.addRecord(new Record("A/C/E"), true);
	}
	
	@Test(expected = UnknownTagException.class)
	public void doEvaluateUnknown() {
		doEvaluateUnknown(SimpleTagsTableFactory.BITSET_FACTORY);
	}
	
	private void doEvaluateUnknown(SimpleTagsTableFactory factory) {
		TagsTable<String> table = new TagsTable<String>(factory);
		table.evaluate("A", true);
	}
	
	@Test(expected = DuplicatedTagException.class)
	public void doDuplicated() {
		doDuplicated(SimpleTagsTableFactory.BITSET_FACTORY);
	}
	
	private void doDuplicated(SimpleTagsTableFactory factory) {
		TagsTable<String> table = new TagsTable<String>(factory);
		table.addTags(Arrays.asList(new String[]{"A","A"}), null);
	}
}
