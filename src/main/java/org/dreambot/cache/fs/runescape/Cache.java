/**
 * Copyright (c) 2014 RSE Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.dreambot.cache.fs.runescape;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.dreambot.cache.fs.runescape.data.CacheIndex;
import org.dreambot.cache.fs.runescape.data.ConfigArchive;
import org.dreambot.cache.io.ByteBufferUtils;
import org.dreambot.cache.io.crypto.Djb2;

/**
 * The {@link Cache} class provides a unified, high-level API for modifying the
 * cache of a Jagex game.
 * 
 * @author Graham
 * @author `Discardedx2
 */
public class Cache implements Closeable {
	
	/**
	 * The file store that backs this cache.
	 */
	private final FileStore store;

	/**
	 * Creates a new {@link Cache} backed by the specified {@link FileStore}.
	 * 
	 * @param store
	 *            The {@link FileStore} that backs this {@link Cache}.
	 * @throws IOException
	 */
	public Cache(FileStore store) throws IOException {
		this.store = store;
	}

	public void close() throws IOException {
		store.close();
	}

	/**
	 * Computes the {@link ChecksumTable} for this cache. The checksum table
	 * forms part of the so-called "update keys".
	 * 
	 * @return The {@link ChecksumTable}.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public ChecksumTable createChecksumTable() throws IOException {
		/* create the checksum table */
		int size = store.getTypeCount();
		ChecksumTable table = new ChecksumTable(size);

		/*
		 * loop through all the reference tables and get their CRC and versions
		 */
		for (int i = 0; i < size; i++) {
			int crc = 0;
			int version = 0;
			int files = 0;
			int archiveSize = 0;
			byte[] whirlpool = new byte[64];

			if (store.hasData()) {
				/*
				 * if there is actually a reference table, calculate the CRC,
				 * version and whirlpool hash
				 */
				ByteBuffer buf = store.read(255, i);
				if (buf != null && buf.limit() > 0) {
					ReferenceTable ref = ReferenceTable.decode(Container.decode(buf).getData());
					crc = ByteBufferUtils.getCrcChecksum(buf);
					version = ref.getVersion();
					files = ref.capacity();
					archiveSize = ref.getArchiveSize();
					buf.position(0);
					whirlpool = ByteBufferUtils.getWhirlpoolDigest(buf);
				}
			}

			table.setEntry(i, new ChecksumTable.Entry(crc, version, files, archiveSize, whirlpool));
		}

		/* return the table */
		return table;
	}

	/**
	 * Gets the number of files of the specified type.
	 * 
	 * @param type
	 *            The type.
	 * @return The number of files.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public int getFileCount(int type) throws IOException {
		return store.getFileCount(type);
	}

	/**
	 * Gets the {@link FileStore} that backs this {@link Cache}.
	 * 
	 * @return The underlying file store.
	 */
	public FileStore getStore() {
		return store;
	}

	/**
	 * Gets the number of index files, not including the meta index file.
	 * 
	 * @return The number of index files.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public int getTypeCount() throws IOException {
		return store.getTypeCount();
	}

	/**
	 * Reads a file from the cache.

	 * @return The file.
	 * @throws IOException
	 *             if an I/O error occurred.
	 */
	public Container read(CacheIndex index, ConfigArchive archive) throws IOException {
		return read(index.getID(), archive.getID());
	}

	/**
	 * Reads a file from the cache.
	 *
	 * @return The file.
	 * @throws IOException
	 *             if an I/O error occurred.
	 */
	public Container read(CacheIndex index, int file) throws IOException {
		return read(index.getID(), file);
	}

	/**
	 * Reads a file from the cache.
	 * 
	 * @param type
	 *            The type of file.
	 * @param file
	 *            The file id.
	 * @return The file.
	 * @throws IOException
	 *             if an I/O error occurred.
	 */
	public Container read(int type, int file) throws IOException {
		/* we don't want people reading/manipulating these manually */
		if (type == 255)
			throw new IOException("Reference tables can only be read with the low level FileStore API!");

		/* delegate the call to the file store then decode the container */
		return Container.decode(store.read(type, file));
	}

	/**
	 * Reads a file from the cache.
	 * 
	 * @param type
	 *            The type of file.
	 * @param file
	 *            The file id.
	 * @param keys
	 *            The decryption keys.
	 * @return The file.
	 * @throws IOException
	 *             if an I/O error occurred.
	 */
	public Container read(int type, int file, int[] keys) throws IOException {
		/* we don't want people reading/manipulating these manually */
		if (type == 255)
			throw new IOException("Reference tables can only be read with the low level FileStore API!");

		/* delegate the call to the file store then decode the container */
		return Container.decode(store.read(type, file), keys);
	}

	/**
	 * Reads a file contained in an archive in the cache.
	 * 
	 * @param type
	 *            The type of the file.
	 * @param file
	 *            The archive id.
	 * @param file
	 *            The file within the archive.
	 * @return The file.
	 * @throws IOException
	 *             if an I/O error occurred.
	 */
	public ByteBuffer read(int type, int file, int member) throws IOException {
		/* grab the container and the reference table */
		Container container = read(type, file);
		Container tableContainer = Container.decode(store.read(255, type));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		/* check if the file/member are valid */
		ReferenceTable.Entry entry = table.getEntry(file);
		if (entry == null || member < 0 || member >= entry.capacity())
			throw new FileNotFoundException();

		/* extract the entry from the archive */
		Archive archive = Archive.decode(container.getData(), entry.capacity());
		return archive.getEntry(member);
	}

}
