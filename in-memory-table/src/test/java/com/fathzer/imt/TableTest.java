package com.fathzer.imt;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.IteratorUtils;
import org.junit.Test;

import static org.junit.Assert.*;

import com.fathzer.imt.TagsTable;
import com.fathzer.imt.implementation.SimpleTagsTableFactory;
import com.fathzer.imt.util.IntIterator;

public class TableTest {

	@Test
	public void test() {
		doTest (new TagsTable<String>(SimpleTagsTableFactory.ROARING_FACTORY));
		doTest (new TagsTable<String>(SimpleTagsTableFactory.BITSET_FACTORY));
	}
	

	private <T extends Bitmap> void doTest(TagsTable<String> table) {
		int index = table.addRecord(new Record("A/C/E"), false);
		assertEquals(0, index);
		List<String> tags = IteratorUtils.toList(table.getTags(index));
		assertEquals(3,tags.size());
		Collections.sort(tags);
		assertEquals("A", tags.get(0));
		assertEquals("C", tags.get(1));
		assertEquals("E", tags.get(2));
		
		assertEquals(1, table.addRecord(new Record("B/D/F"), false));
		RecordSet<String> recordSet = table.evaluate("A && C");
		IntIterator iterator = recordSet.getIds();
		assertEquals(0, iterator.next());
		assertEquals(1, recordSet.size());
		
		assertEquals(0, table.evaluate("A && B").size());
		
		recordSet = table.evaluate("A || D");
		iterator = recordSet.getIds();
		assertEquals(0, iterator.next());
		assertEquals(1, iterator.next());
		assertEquals(2, recordSet.size());
		
		recordSet = table.evaluate("!E && A||D");
		iterator = recordSet.getIds();
		assertEquals(1, iterator.next());
		assertEquals(1, recordSet.size());
		
		recordSet = table.evaluate("!E");
		assertEquals(1, recordSet.size());
		iterator = recordSet.getIds();
		assertEquals(1, iterator.next());
	}
	
	@Test(expected = UnknownTagException.class)
	public void doAddUnknown() {
		doAddUnknown(SimpleTagsTableFactory.BITSET_FACTORY);
	}
	
	private <T extends Bitmap> void doAddUnknown(SimpleTagsTableFactory factory) {
		TagsTable<String> table = new TagsTable<String>(factory);
		table.addRecord(new Record("A/C/E"), true);
	}
	
	@Test(expected = UnknownTagException.class)
	public void doEvaluateUnknown() {
		doEvaluateUnknown(SimpleTagsTableFactory.BITSET_FACTORY);
	}
	
	private <T extends Bitmap> void doEvaluateUnknown(SimpleTagsTableFactory factory) {
		TagsTable<String> table = new TagsTable<String>(factory);
		table.evaluate("A");
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
