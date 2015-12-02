package com.fathzer.imt;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

import com.fathzer.imt.TagsTable;
import com.fathzer.imt.implementation.BitSetBitmap;
import com.fathzer.imt.implementation.RoaringBitmap;
import com.fathzer.imt.implementation.SimpleTagsTableFactory;
import com.fathzer.imt.util.IntIterator;

public class TableTest {

	@Test
	public void test() {
		doTest (new TagsTable<String, RoaringBitmap>(SimpleTagsTableFactory.ROARING_FACTORY));
		doTest (new TagsTable<String, BitSetBitmap>(SimpleTagsTableFactory.BITSET_FACTORY));
//		return new TagsTable<String, T>(new SimpleTableFactory<T>(BitmapAdapter.EWAH64));
	}
	

	private <T extends Bitmap> void doTest(TagsTable<String, T> table) {
		int index = table.addRecord(new Record("A/C/E"), false);
		assertEquals(0, index);
		
		assertEquals(1, table.addRecord(new Record("B/D/F"), false));
		RecordSet<String, T> recordSet = table.evaluate("A && C");
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
	
	private <T extends Bitmap> void doAddUnknown(SimpleTagsTableFactory<T> factory) {
		TagsTable<String, T> table = new TagsTable<String, T>(factory);
		table.addRecord(new Record("A/C/E"), true);
	}
	
	@Test(expected = UnknownTagException.class)
	public void doEvaluateUnknown() {
		doEvaluateUnknown(SimpleTagsTableFactory.BITSET_FACTORY);
	}
	
	private <T extends Bitmap> void doEvaluateUnknown(SimpleTagsTableFactory<T> factory) {
		TagsTable<String, T> table = new TagsTable<String, T>(factory);
		table.evaluate("A");
	}
	
	@Test(expected = DuplicatedTagException.class)
	public void doDuplicated() {
		doDuplicated(SimpleTagsTableFactory.BITSET_FACTORY);
	}
	
	private <T extends Bitmap> void doDuplicated(SimpleTagsTableFactory<T> factory) {
		TagsTable<String, T> table = new TagsTable<String, T>(factory);
		table.addTags(Arrays.asList(new String[]{"A","A"}), null);
	}
}
