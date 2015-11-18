package com.fathzer.imt.dummy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;

public class SynchroTest {
	private enum KIND {MAP, SYNCHRO, CONCURRENT, OFF_MEM}
	private static final boolean STOP_BEFORE_TEST = Boolean.getBoolean("stopBeforeTest");
	
	public static void main (String[] args) throws IOException {
		int mapSize = 10000;
		int keysSize = 100;
		int calls = 10000;
		int averageValueSize = 1024;
		
		Collection<String> keys = getKeys(keysSize, mapSize);

		int t1 = doTest(KIND.SYNCHRO, mapSize, averageValueSize, keys, calls, "Synchronized");
		int t2 = doTest(KIND.CONCURRENT, mapSize, averageValueSize, keys, calls, "Concurrent");
		int t3 = doTest(KIND.MAP, mapSize, averageValueSize, keys, calls, "Not synchronized");
		int t4 = doTest(KIND.OFF_MEM, mapSize, averageValueSize, keys, calls, "Off heap");
	}
	
	private static int doTest(KIND kind, int mapSize, int averageValueSize, final Collection<String> keys, int calls, String header) {
		final Map<String, BitSet> map = createMap(kind, mapSize, averageValueSize);
		try {
			int nb1 = doTest(keys, calls, 1, header, map);
			int nb4 = doTest(keys, calls, 4, header, map);
			if (4*nb1!=nb4) {
				throw new RuntimeException("not same result");
			} else {
				return nb1;
			}
		} finally {
			if (KIND.OFF_MEM.equals(kind)) {
				((ChronicleMap<String, BitSet>)map).close();
			}
			System.gc();
		}
	}
	
	static void waitUserApproval() {
		if (STOP_BEFORE_TEST) {
			System.out.println("GC done - Press return to continue");
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static int doTest(final Collection<String> keys, int calls, int nbThreads, String header,
			final Map<String, BitSet> map) {
		System.gc();
		long start = System.nanoTime();
		int nb = 0;
		for (int i = 0; i < calls; i++) {
			Collection<Callable<Integer>> tasks = new ArrayList<>(); 
			for (int j = 0; j < nbThreads; j++) {
				tasks.add(new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						return doTest(map, keys);
					}
				});
			}
			List<Future<Integer>> result = getPool(nbThreads).invokeAll(tasks);
			for (Future<Integer> future : result) {
				try {
					nb += future.get().intValue();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}
			}
		}
		long time = (System.nanoTime()-start)/1000000;
		System.out.println(header+", "+nbThreads+" threads in "+time+"ms");
		return nb;
	}
	
	private static Map<String, BitSet> createMap(KIND kind, int nb, int averageValueSize) {
		Map<String, BitSet> map;
		if (KIND.MAP.equals(kind)) {
			map = new HashMap<String, BitSet>(nb);
		} else if (KIND.SYNCHRO.equals(kind)) {
			map = new Hashtable<>(nb);
		} else if (KIND.CONCURRENT.equals(kind)) {
			map = new ConcurrentHashMap<>(nb);
		} else if (KIND.OFF_MEM.equals(kind)) {
//		    File file = new File("testOMap.mem");
		    ChronicleMapBuilder<String, BitSet> builder = ChronicleMapBuilder.of(String.class, BitSet.class)
		    	.entries(nb).averageValueSize(averageValueSize);
			map = builder.create();
//			map = builder.createPersistedTo(file);
//			map.clear();
		} else {
			throw new IllegalArgumentException();
		}
		fillMap(map, nb);
		return map;
	}
	
	private static void fillMap(Map<String, BitSet> map, int nb) {
		for (int i=0;i<nb;i++) {
			BitSet bitSet = new BitSet();
			bitSet.set(i);
			map.put(Integer.toString(i), bitSet);
		}
	}
	
	private static Collection<String> getKeys(int nb, int mapSize) {
		List<String> keys = new ArrayList<>(nb);
		for (int i = 0; i < nb; i++) {
			keys.add(Integer.toString(new Random().nextInt(mapSize)));
		}
		return keys;
	}
	
	static List<ForkJoinPool> pools = new ArrayList<>();
	private static ForkJoinPool getPool(int nbThreads) {
		while(pools.size()<nbThreads) {
			pools.add(null);
		}
		ForkJoinPool pool = pools.get(nbThreads-1);
		if (pool == null) {
//			System.out.println("creating a ForkJoinPool with "+nbThreads+" threads");
			pool = new ForkJoinPool(nbThreads);
			pools.set(nbThreads-1, pool);
		}
		return pool;
	}

	private static int doTest(Map<String, BitSet> map, Collection<String> keys) {
		BitSet dummy = new BitSet();
		for (String key:keys) {
			dummy.or(map.get(key));
		}
		return dummy.cardinality();
	}
}
