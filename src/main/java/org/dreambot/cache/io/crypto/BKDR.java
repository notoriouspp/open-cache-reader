package org.dreambot.cache.io.crypto;

/**
 * An implementation of the K&R hash function.
 * @author Graham
 * @author `Discardedx2
 */
public final class BKDR {

	/**
	 * An implementation of K&R hash function.
	 * @param str The string to hash.
	 * @return The hash code.
	 */
	public static int hash(String str) {
		int hash = 0;
		for (int i = 0; i < str.length(); i++) {
			hash = str.charAt(i) + ((hash << 5) - hash);
		}
		return hash;
	}

	/**
	 * Default private constructor to prevent instantiation.
	 */
	private BKDR() {
		
	}

}
