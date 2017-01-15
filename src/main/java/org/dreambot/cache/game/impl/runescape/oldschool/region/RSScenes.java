package org.dreambot.cache.game.impl.runescape.oldschool.region;

import org.dreambot.cache.fs.runescape.Container;
import org.dreambot.cache.fs.runescape.ReferenceTable;
import org.dreambot.cache.game.impl.runescape.oldschool.render.Sprite;
import org.dreambot.cache.tools.CacheManager;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Robert.
 * Time :   17:17.
 */
public class RSScenes {
    public static BufferedImage[] SCENES_CACHE = new BufferedImage[100];

    static {
        try {
            Container container = Container.decode(CacheManager.get().getStore().read(255, 8));
            ReferenceTable table = ReferenceTable.decode(container.getData());
            int index = CacheManager.getFileID(table, "mapscene");
            ByteBuffer data = Container.decode(CacheManager.get().getStore().read(8, index)).getData();
            Sprite decode = Sprite.decode(data);
            for (int i = 0; i < decode.size(); i++) {
                SCENES_CACHE[i] = decode.getFrame(i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
