package com.fathzer.imt;

import java.util.Collection;
import java.util.Set;

/** An object that maps keys to {@link Bitmap}.
 * This interface is a subset of the java.util.Map interface.
 * @author Jean-Marc Astesana
 *
 * @param <T> The type of the keys.
 */
public interface BitmapMap<T> {
	/** Tests whether this map contains a key.
	 * @param key a key.
	 * @return true if the map contains the key.
	 */
	boolean containsKey(T key);
	
	/** Gets the bitmap to which the specified key is mapped.
	 * @param key The key.
	 * @return a Bitmap or null if the key is unknown.
	 */
	Bitmap get(T key);
	
	/** Puts a bitmap in this map.
	 * @param key The key.
	 * @param value The bitmap to put in the table.
	 * @return the previous Bitmap mapped to the key (null if the key was unknown).
	 */
	Bitmap put(T key, Bitmap value);
	
	/** Gets a Set view of the keys contained in this map.
	 * @return a Set of keys.
	 */
	Set<T> keySet();
	
	/** Gets a Collection view of the bitmaps contained in this map.
	 * @return a Collection of bitmaps.
	 */
	Collection<Bitmap> values();
}
