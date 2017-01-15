package org.dreambot.util;

/**
 * Created by Robert.
 * Time :   00:18.
 */
public class Constants {
    /**
     * The tile size in pixels. 4 is suggested cuz wall is always 1, so we can't really shrink it if we want good looking walls.
     */
    public static final int TILE_SIZE = 4;

    /**
     * The default number of planes
     */
    public static final int PLANES = 4;

    /**
     * The block size
     */
    public static final int BLOCK_SIZE = 64;

    /**
     * The max blocks on RSMap
     */
    public static final int MAX_BLOCKS = 256;//256 is real, but its mem waste as fuck
}
