package com.fathzer.imt.dummy;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.roaringbitmap.RoaringBitmap;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

public abstract class MapTest<T> {
	private static class IntegerTest extends MapTest<Integer> {
		protected IntegerTest() {
			super(Integer.class);
		}

		@Override
		protected boolean isOk(int index, Integer value) {
			return ((Integer) value).intValue()==index;
		}

		@Override
		protected Integer getValue(int index) {
			return index;
		}
	};
	
	private static class RoaringTest extends MapTest<RoaringBitmap> {
		protected RoaringTest() {
			super(RoaringBitmap.class);
		}

		@Override
		protected RoaringBitmap getValue(int index) {
			RoaringBitmap bitmap = new RoaringBitmap();
			bitmap.add(index);
			return bitmap;
		}
	};
	
	private static class BitSetTest extends MapTest<BitSet> {
		protected BitSetTest() {
			super(BitSet.class);
		}

		@Override
		protected BitSet getValue(int index) {
			BitSet bitmap = new BitSet();
			bitmap.set(index);
			return bitmap;
		}
	};

	
	public static void main(String[] args) throws IOException {
		new RoaringTest().doTest();
	}

	private Class<T> type;
	
	protected MapTest(Class<T> type) {
		this.type = type;
	}
	protected void doTest() throws IOException {
		test("Not synchronized", new HashMap<String, T>());
		test("Synchronized", new Hashtable<String, T>());
		test("Concurrent", new ConcurrentHashMap<String, T>());
	
	  File file = new File("testOMap.mem");
	  if (file.exists()) {
	  	file.delete();
	  }
	  ChronicleMapBuilder<String, T> builder = ChronicleMapBuilder.of(String.class, type).entries(1000000);
		Map<String, T> omap = builder.createPersistedTo(file);
		try {
			test("Off memory", omap);
		} finally {
			((ChronicleMap<String, T>)omap).close();
		}
	}


	private void test(String message, Map<String, T> map) {
		map.clear();
		for (int i = 0; i < 10000; i++) {
			map.put(Integer.toString(i), getValue(i));
		}
		Random rnd = new Random();
		System.gc();
		SynchroTest.waitUserApproval();
		long start = System.nanoTime();
		for (int i = 0; i < 1000000; i++) {
			int key = rnd.nextInt(map.size());
			T value = map.get(Integer.toString(key));
			if (value==null || !isOk(key, value)) {
				throw new RuntimeException("key "+key+"->"+value);
			}
		}
		System.out.println (message+" ended in "+(System.nanoTime()-start)/1000000+"ms");
	}
	
	protected abstract T getValue(int index);
	
	protected boolean isOk(int index, T value) {
		return true;
	}
}
