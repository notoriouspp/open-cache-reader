package org.dreambot.cache.tools;

import org.dreambot.cache.fs.runescape.Cache;
import org.dreambot.cache.fs.runescape.FileStore;
import org.dreambot.cache.fs.runescape.ReferenceTable;
import org.dreambot.cache.game.impl.runescape.Runescape;
import org.dreambot.cache.game.impl.runescape.oldschool.definition.ObjectDefinition;
import org.dreambot.cache.game.impl.runescape.oldschool.render.Textures;
import org.dreambot.cache.io.crypto.BKDR;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 1/14/2017
 */
public class CacheManager extends Cache {

    private static CacheManager cache;

    public CacheManager(FileStore store) throws IOException {
        super(store);
    }

    public static synchronized Cache get() {
        if (cache == null) {
            try {
                cache = new CacheManager(FileStore.open(Runescape.OLD_SCHOOL.getDirectory()));
                ObjectDefinition.initialize(cache);
                Textures.initialize(cache);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cache;
    }

    public static int getFileID(ReferenceTable table, String name) {
        if (name == null) {
            return -1;
        }
        int hash = BKDR.hash(name);
        for (int entry = 0; entry < table.size(); entry++) {
            if (table.getEntry(entry).getIdentifier() == hash) {
                return entry;
            }
        }
        return -1;
    }

    public ByteBuffer read(ReferenceTable table, int index, String name) {
        try {
            int id = getFileID(table, name);
            if (id == -1) {
                return null;
            }
            return getStore().read(index, id);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
