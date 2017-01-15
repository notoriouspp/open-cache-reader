package org.dreambot;

import org.junit.Assert;
import org.junit.Test;
import org.dreambot.cache.fs.runescape.Cache;
import org.dreambot.cache.fs.runescape.FileStore;
import org.dreambot.cache.game.impl.runescape.Runescape;

import java.io.File;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 1/14/2017
 */
public class CacheVerifyTest {

    @Test
    public void cacheVerificationTest() {
        try {
            File root = new File(Runescape.OLD_SCHOOL.getDirectory());
            Assert.assertTrue(root.exists());
            FileStore open = FileStore.open(root);
            Assert.assertNotNull(open);
            Cache cache = new Cache(open);
            Assert.assertNotNull(cache);
            System.out.println(('\u8000') * 1);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
