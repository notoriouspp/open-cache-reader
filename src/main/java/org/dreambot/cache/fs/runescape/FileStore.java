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

import org.dreambot.cache.fs.runescape.data.CacheIndex;
import org.dreambot.cache.fs.util.FileChannelUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


/**
 * A file store holds multiple files inside a "virtual" file system made up of
 * several index files and a single data file.
 * 
 * @author Graham
 * @author `Discardedx2
 */
public final class FileStore implements Closeable {

	private final File root;

	/**
	 * Creates the file store stored in the specified directory.
	 * 
	 * @param root
	 *            The directory containing the index and data files.
	 * @param indices
	 *            The amount of indices
	 * @return The file store.
	 * @throws FileNotFoundException
	 *             if any of the {@code main_file_cache.*} files could not be
	 *             found.
	 */
	public static FileStore create(String root, int indices) throws IOException {
		return create(new File(root), indices);
	}

	/**
	 * Creates the file store stored in the specified directory.
	 * 
	 * @param root
	 *            The directory containing the index and data files.
	 * @param indices
	 *            The amount of indices
	 * @return The file store.
	 * @throws FileNotFoundException
	 *             if any of the {@code main_file_cache.*} files could not be
	 *             found.
	 */
	public static FileStore create(File root, int indices) throws IOException {
		if (!root.exists() && !root.mkdirs())
			throw new IOException();

		for (int i = 0; i < indices; i++) {
			File index = new File(root, "main_file_cache.idx" + i);
			if (!index.createNewFile())
				throw new IOException();
		}

		File meta = new File(root, "main_file_cache.idx255");
		if (!meta.createNewFile())
			throw new IOException();

		File data = new File(root, "main_file_cache.dat2");
		if (!data.createNewFile())
			throw new IOException();

		return open(root);
	}

	/**
	 * Opens the file store stored in the specified directory.
	 * 
	 * @param root
	 *            The directory containing the index and data files.
	 * @return The file store.
	 * @throws FileNotFoundException
	 *             if any of the {@code main_file_cache.*} files could not be
	 *             found.
	 */
	@SuppressWarnings("resource")
	public static FileStore open(File root) throws FileNotFoundException {
		File data = new File(root, "main_file_cache.dat2");
		if (!data.exists())
			throw new FileNotFoundException();
		RandomAccessFile raf = new RandomAccessFile(data, "rw");
		FileChannel dataChannel = raf.getChannel();

		List<FileChannel> indexChannels = new ArrayList<FileChannel>();
		for (int i = 0; i < 254; i++) {
			File index = new File(root, "main_file_cache.idx" + i);
			if (!index.exists())
				break;

			raf = new RandomAccessFile(index, "rw");
			FileChannel indexChannel = raf.getChannel();
			indexChannels.add(indexChannel);
		}

		if (indexChannels.isEmpty())
			throw new FileNotFoundException();

		File meta = new File(root, "main_file_cache.idx255");
		if (!meta.exists())
			throw new FileNotFoundException();

		raf = new RandomAccessFile(meta, "rw");
		FileChannel metaChannel = raf.getChannel();

		return new FileStore(root, dataChannel, indexChannels.toArray(new FileChannel[0]), metaChannel);
	}

	/**
	 * Opens the file store stored in the specified directory.
	 * 
	 * @param root
	 *            The directory containing the index and data files.
	 * @return The file store.
	 * @throws FileNotFoundException
	 *             if any of the {@code main_file_cache.*} files could not be
	 *             found.
	 */
	public static FileStore open(String root) throws FileNotFoundException {
		return open(new File(root));
	}

	/**
	 * The data file.
	 */
	private final FileChannel dataChannel;

	/**
	 * The index files.
	 */
	private final FileChannel[] indexChannels;

	/**
	 * The 'meta' index files.
	 */
	private final FileChannel metaChannel;

	/**
	 * Creates a new file store.
	 * 
	 * @param data
	 *            The data file.
	 * @param indexes
	 *            The index files.
	 * @param meta
	 *            The 'meta' index file.
	 */
	public FileStore(File root, FileChannel data, FileChannel[] indexes, FileChannel meta) {
		this.root = root;
		this.dataChannel = data;
		this.indexChannels = indexes;
		this.metaChannel = meta;
	}

	public File getRoot() {
		return root;
	}

	public void close() throws IOException {
		dataChannel.close();

		for (FileChannel channel : indexChannels)
			channel.close();

		metaChannel.close();
	}

	public boolean hasData() throws IOException {
		return dataChannel.size() > 0;
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
		if ((type < 0 || type >= indexChannels.length) && type != 255)
			throw new FileNotFoundException();

		if (type == 255)
			return (int) (metaChannel.size() / Index.SIZE);
		return (int) (indexChannels[type].size() / Index.SIZE);
	}

	/**
	 * Gets the number of index files, not including the meta index file.
	 * 
	 * @return The number of index files.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public int getTypeCount() throws IOException {
		return indexChannels.length;
	}

	/**
	 * Reads a file.
	 * 
	 * @param index The index file
	 * @param archive The archive file
	 *
	 * @return A {@link ByteBuffer} containing the contents of the file.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public ByteBuffer read(CacheIndex index, CacheIndex archive) throws IOException {
		return read(index.getID(), archive.getID());
	}

	/**
	 * Reads a file.
	 * 
	 * @param type
	 *            The type of the file.
	 * @param id
	 *            The id of the file.
	 * @return A {@link ByteBuffer} containing the contents of the file.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@SuppressWarnings("resource")
	public ByteBuffer read(int type, int id) throws IOException {
		if ((type < 0 || type >= indexChannels.length) && type != 255)
			throw new FileNotFoundException();

		FileChannel indexChannel = type == 255 ? metaChannel : indexChannels[type];

		long ptr = (long) id * (long) Index.SIZE;
		if (ptr < 0 || ptr >= indexChannel.size()) {
			throw new FileNotFoundException();
		}

		ByteBuffer buf = ByteBuffer.allocate(Index.SIZE);
		FileChannelUtils.readFully(indexChannel, buf, ptr);

		Index index = Index.decode((ByteBuffer) buf.flip());

		ByteBuffer data = ByteBuffer.allocate(index.getSize());
		buf = ByteBuffer.allocate(Sector.SIZE);

		int chunk = 0, remaining = index.getSize();
		ptr = (long) index.getSector() * (long) Sector.SIZE;
		do {
			buf.clear();
			FileChannelUtils.readFully(dataChannel, buf, ptr);
			boolean extended = id > 0xFFFF;
			Sector sector = extended ? Sector.decodeExtended((ByteBuffer) buf.flip())
					: Sector.decode((ByteBuffer) buf.flip());
			int dataSize = extended ? Sector.EXTENDED_DATA_SIZE : Sector.DATA_SIZE;
			if (remaining > dataSize) {
				data.put(sector.getData(), 0, dataSize);
				remaining -= dataSize;

				if (sector.getType() != type)
					throw new IOException("File type mismatch.");

				if (sector.getId() != id)
					throw new IOException("File id mismatch.");

				if (sector.getChunk() != chunk++)
					throw new IOException("Chunk mismatch.");

				ptr = (long) sector.getNextSector() * (long) Sector.SIZE;
			} else {
				data.put(sector.getData(), 0, remaining);
				remaining = 0;
			}
		} while (remaining > 0);
		return (ByteBuffer) data.flip();
	}

}
