package com.fathzer.imt;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

import com.fathzer.imt.implementation.SimpleTagsTableFactory;
import com.fathzer.imt.util.IntIterator;

public class SerializationTest {
	@Test
	public void test() {
		TagsTable<String> table = new MyTable();
		table.addRecord(new Record("A/C"), false);
		table.addRecord(new Record("B/D"), false);
		table.deleteRecord(0);
//		table = table.getLocked();
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

	private TagsTable<String> fromBytes(byte[] bytes) {
		try {
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
			try {
				return (TagsTable<String>) in.readObject();
			} finally {
				in.close();
			}
		} catch (IOException e) {
			Assert.fail(e.toString());
			return null;
		} catch (ClassNotFoundException e) {
			Assert.fail(e.toString());
			return null;
		}
	}

	private byte[] toBytes(TagsTable<String> table) {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(bo);
			try {
				out.writeObject(table);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			Assert.fail(e.toString());
		}
		return bo.toByteArray();
	}

	public static class MyTable extends TagsTable<String> implements Externalizable {
		public MyTable() {
			super(SimpleTagsTableFactory.BITSET_FACTORY);
		}

		@Override
		public void writeExternal(ObjectOutput out) throws IOException {
			out.writeInt(getSize());
			Iterator<String> tags = this.getTags();
			while (tags.hasNext()) {
				String tag = tags.next();
				out.writeObject(tag);
				out.writeObject(getBitMapIndex(tag));
			}
			out.writeObject(null);
			Bitmap ok = evaluate("!X||X", false).clone();
			ok.not(getSize());
			out.writeObject(ok);
			out.writeBoolean(isLocked());
		}

		@Override
		public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
			int size = in.readInt();
			Iterator<String> emptyRecord = new Iterator<String>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public String next() {
					throw new NoSuchElementException();
				}
			};
			for (int i = 0; i < size; i++) {
				addRecord(emptyRecord, true);
			}
			List<String> tags = new ArrayList<String>();
			List<IntIterator> tagRecords = new ArrayList<>();
			for (String tag = (String) in.readObject(); tag != null; tag = (String) in.readObject()) {
				tags.add(tag);
				tagRecords.add(((Bitmap)in.readObject()).getIterator());
			}
			addTags(tags, tagRecords);
			Bitmap deleted = (Bitmap) in.readObject();
			if (!deleted.isEmpty()) {
				IntIterator iterator = deleted.getIterator();
				while (iterator.hasNext()) {
					deleteRecord(iterator.next());
				}
			}
			if (in.readBoolean()) {
				//TODO how to restore locked state;
				fail("Damed, it's locked !");
			}
		}
	}
}
