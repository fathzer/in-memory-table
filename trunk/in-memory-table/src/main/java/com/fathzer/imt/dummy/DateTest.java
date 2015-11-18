package com.fathzer.imt.dummy;

import java.util.Random;

import org.roaringbitmap.RoaringBitmap;

public class DateTest {

	public static void main(String[] args) {
		Random rnd = new Random();
		int nbDates = 730;
		int nbVeh = 1000000;
		RoaringBitmap[] table = new RoaringBitmap[nbDates];
		for (int i = 0; i < table.length; i++) {
			table[i] = new RoaringBitmap();
		}
		for (int i = 0; i < nbVeh; i++) {
			table[rnd.nextInt(table.length)].add(i);
		}
		int size = 0;
		for (RoaringBitmap roaringBitmap : table) {
			size += roaringBitmap.getSizeInBytes();
		}
		System.out.println (table.length+" dates on "+nbVeh+" vehicles: "+size/1024+"kBytes");
	}

}
