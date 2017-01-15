package org.dreambot.cache.fs.runescape;

import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 1/14/2017
 */
public class XTEALoader {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.dreambot.cache.fs.runescape.keys");
    public static final int[] NULL_KEYS = new int[4];

    public static int[] getKeys(int regionID){
        String string = RESOURCE_BUNDLE.getString(Integer.toString(regionID));
        if(!string.isEmpty()){
            return Stream.of(string.split(",")).mapToInt(Integer::valueOf).toArray();
        }
        return new int[4];
    }

}
