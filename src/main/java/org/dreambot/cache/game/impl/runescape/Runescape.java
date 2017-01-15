package org.dreambot.cache.game.impl.runescape;

import org.dreambot.cache.game.Game;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 *
 * @author  : Notorious
 * @version  : 0.0.1
 * @since  : 2/11/2015
 * Time : 5:52 PM
 */
public enum Runescape implements Game {

    /**
     * The default location of the Runescape 3 cache directory.
     */
    RUNESCAPE_3(String.valueOf(String.valueOf(System.getProperty("user.home"))
            + File.separator) + "jagexcache"
            + File.separator + "runescape"
            + File.separator + "LIVE" + File.separator,
            40),
    /**
     * The default location of the Old School Runescape cache directory.
     */
    OLD_SCHOOL(String.valueOf(String.valueOf(System.getProperty("user.home"))
            + File.separator) + "jagexcache"
            + File.separator + "oldschool"
            + File.separator + "LIVE" + File.separator,
            16);

    private String location;
    private int indexCount;

    Runescape(String location, int indexCount) {
        this.location = location;
        this.indexCount = indexCount;
    }

    /**
     * Gets the String path of the directory containing the cache files.
     *
     * @return the String path of the directory containing of the cache file
     */
    @Override
    public String getDirectory() {
        return location;
    }

    /**
     * Gets index count of the cache system.
     *
     * @return the index count of the cache system
     */
    @Override
    public int getIndexCount() {
        return indexCount;
    }
}
