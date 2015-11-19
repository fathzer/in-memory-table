package com.fathzer.imt;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

import com.fathzer.imt.TagsTable;
import com.fathzer.imt.implementation.SimpleTableFactory;
import com.fathzer.imt.util.IntIterator;
import com.fathzer.imt.BitmapAdapter;

public class TableTest {

	@Test
	public void test() {
		doTest (getTable(BitmapAdapter.ROARING));
		doTest (getTable(BitmapAdapter.BITSET));
		doTest (getTable(BitmapAdapter.EWAH64));
		doTest (getTable(BitmapAdapter.EWAH32));
	}
	
	private <T> TagsTable<String, T> getTable(BitmapAdapter<T> adapter) {
		return new TagsTable<String, T>(new SimpleTableFactory<T>(adapter));
	}

	private <T> void doTest(TagsTable<String, T> table) {
		table.addRecord(new Record("A/C/E"), false);
		table.addRecord(new Record("B/D/F"), false);
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
		doAddUnknown(BitmapAdapter.BITSET);
	}
	
	private <T> void doAddUnknown(BitmapAdapter<T> adapter) {
		TagsTable<String, T> table = new TagsTable<String, T>(new SimpleTableFactory<T>(adapter));
		table.addRecord(new Record("A/C/E"), true);
	}
	
	@Test(expected = UnknownTagException.class)
	public void doEvaluateUnknown() {
		doEvaluateUnknown(BitmapAdapter.BITSET);
	}
	
	private <T> void doEvaluateUnknown(BitmapAdapter<T> adapter) {
		TagsTable<String, T> table = new TagsTable<String, T>(new SimpleTableFactory<T>(adapter));
		table.evaluate("A");
	}
	
	@Test(expected = DuplicatedTagException.class)
	public void doDuplicated() {
		doDuplicated(BitmapAdapter.BITSET);
	}
	
	private <T> void doDuplicated(BitmapAdapter<T> adapter) {
		TagsTable<String, T> table = new TagsTable<String, T>(new SimpleTableFactory<T>(adapter));
		table.addTags(Arrays.asList(new String[]{"A","A"}), null);
	}
}
