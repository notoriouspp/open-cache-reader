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

import org.dreambot.cache.fs.util.CompressionUtils;
import org.dreambot.cache.io.crypto.Xtea;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * A {@link Container} holds an optionally compressed file. This class can be
 * used to decompress and compress containers. A container can also have a two
 * byte trailer which specifies the version of the file within it.
 * 
 * @author Graham
 * @author `Discardedx2
 */
public final class Container {

	/**
	 * This type indicates that no compression is used.
	 */
	public static final int COMPRESSION_NONE = 0;

	/**
	 * This type indicates that BZIP2 compression is used.
	 */
	public static final int COMPRESSION_BZIP2 = 1;

	/**
	 * This type indicates that GZIP compression is used.
	 */
	public static final int COMPRESSION_GZIP = 2;

	/**
	 * Decodes and decompresses the container.
	 * 
	 * @param buffer
	 *            The buffer.
	 * @return The decompressed container.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static Container decode(ByteBuffer buffer) throws IOException {
		return Container.decode(buffer, new int[4]);
	}

	/**
	 * Decodes and decompresses the container.
	 * 
	 * @param buffer
	 *            The buffer.
	 * @param keys
	 *            The decryption keys
	 * @return The decompressed container.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static Container decode(ByteBuffer buffer, int[] keys) throws IOException {
		/* decode the type and length */
		int type = buffer.get() & 0xFF;
		int length = buffer.getInt();

		/* decrypt */
		if (keys[0] != 0 || keys[1] != 0 || keys[2] != 0 || keys[3] != 0) {
			Xtea.decipher(buffer, 5, length + (type == COMPRESSION_NONE ? 5 : 9), keys);
		}

		/* check if we should decompress the data or not */
		if (type == COMPRESSION_NONE) {
			/* simply grab the data and wrap it in a buffer */
			byte[] temp = new byte[length];
			buffer.get(temp);
			ByteBuffer data = ByteBuffer.wrap(temp);

			/* decode the version if present */
			int version = -1;
			if (buffer.remaining() >= 2) {
				version = buffer.getShort();
			}

			/* and return the decoded container */
			return new Container(type, data, version);
		} else {
			/* grab the length of the uncompressed data */
			int uncompressedLength = buffer.getInt();

			/* grab the data */
			byte[] compressed = new byte[length];
			buffer.get(compressed);

			/* uncompress it */
			byte[] uncompressed;
			if (type == COMPRESSION_BZIP2) {
				uncompressed = CompressionUtils.bunzip2(compressed);
			} else if (type == COMPRESSION_GZIP) {
				uncompressed = CompressionUtils.gunzip(compressed);
			} else {
				throw new IOException("Invalid compression type");
			}

			/* check if the lengths are equal */
			if (uncompressed.length != uncompressedLength) {
				throw new IOException("Length mismatch. [ " + uncompressed.length + ", " + uncompressedLength + " ]");
			}

			/* decode the version if present */
			int version = -1;
			if (buffer.remaining() >= 2) {
				version = buffer.getShort();
			}

			/* and return the decoded container */
			return new Container(type, ByteBuffer.wrap(uncompressed), version);
		}
	}

	/**
	 * The type of compression this container uses.
	 */
	private int type;

	/**
	 * The decompressed data.
	 */
	private ByteBuffer data;

	/**
	 * The version of the file within this container.
	 */
	private int version;

	/**
	 * Creates a new unversioned container.
	 * 
	 * @param type
	 *            The type of compression.
	 * @param data
	 *            The decompressed data.
	 */
	public Container(int type, ByteBuffer data) {
		this(type, data, -1);
	}

	/**
	 * Creates a new versioned container.
	 * 
	 * @param type
	 *            The type of compression.
	 * @param data
	 *            The decompressed data.
	 * @param version
	 *            The version of the file within this container.
	 */
	public Container(int type, ByteBuffer data, int version) {
		this.type = type;
		this.data = data;
		this.version = version;
	}


	/**
	 * Gets the decompressed data.
	 * 
	 * @return The decompressed data.
	 */
	public ByteBuffer getData() {
		return data.asReadOnlyBuffer();
	}

	/**
	 * Gets the type of this container.
	 * 
	 * @return The compression type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Gets the version of the file in this container.
	 * 
	 * @return The version of the file.
	 * @throws IllegalArgumentException
	 *             if this container is not versioned.
	 */
	public int getVersion() {
		if (!isVersioned())
			throw new IllegalStateException();

		return version;
	}

	/**
	 * Checks if this container is versioned.
	 * 
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean isVersioned() {
		return version != -1;
	}

	/**
	 * Removes the version on this container so it becomes unversioned.
	 */
	public void removeVersion() {
		this.version = -1;
	}

	/**
	 * Sets the type of this container.
	 * 
	 * @param type
	 *            The compression type.
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Sets the version of this container.
	 * 
	 * @param version
	 *            The version.
	 */
	public void setVersion(int version) {
		this.version = version;
	}

}
