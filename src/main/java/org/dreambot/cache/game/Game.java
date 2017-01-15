package org.dreambot.cache.game;

/**
 * Created with IntelliJ IDEA.
 *
 * @author  : Notorious
 * @version  : 0.0.1
 * @since  : 8/22/2015
 * Time : 9:16 PM
 */
public interface Game {

    /**
     * Gets the String path of the directory containing the cache files.
     *
     * @return the String path of the directory containing of the cache file
     */
    String getDirectory();

    /**
     * Gets index count of the cache system.
     *
     * @return the index count of the cache system
     */
     int getIndexCount();
}
