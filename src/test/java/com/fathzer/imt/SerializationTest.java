package com.fathzer.imt;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.fathzer.imt.implementation.SimpleTagsTableFactory;

public class SerializationTest {
	@Test
	public void test() throws IOException, ClassNotFoundException {
		TagsTable<String> table = new TagsTable<>(SimpleTagsTableFactory.BITSET_FACTORY);
		table.addRecord(new Record("A/C"), false);
		table.addRecord(new Record("B/D"), false);
		table.deleteRecord(0);
		table = table.getLocked();
		byte[] bytes = toBytes(table);
		test(table);

		table = fromBytes(bytes);
		test(table);
	}
	
	private void test(TagsTable<String> table) {
		assertEquals(1, table.getLogicalSize());
		assertEquals(2, table.getSize());
		assertEquals(0, table.evaluate("A", true).getCardinality());
		Bitmap dSet = table.evaluate("D", true);
		assertEquals(1, dSet.getCardinality());
		assertTrue(dSet.contains(1));
		if (!table.isLocked()) {
			table.addRecord(new Record("E"), false);
			assertEquals(2, table.getSize());
		}
	}

	@SuppressWarnings("unchecked")
	private TagsTable<String> fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
		try {
			return (TagsTable<String>) in.readObject();
		} finally {
			in.close();
		}
	}

	private byte[] toBytes(TagsTable<String> table) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bo);
		try {
			out.writeObject(table);
		} finally {
			out.close();
		}
		return bo.toByteArray();
	}
}
