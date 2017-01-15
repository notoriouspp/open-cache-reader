package org.dreambot.cache.game.impl.runescape.oldschool.region.tile;

import org.dreambot.cache.fs.runescape.Archive;
import org.dreambot.cache.fs.runescape.Container;
import org.dreambot.cache.fs.runescape.ReferenceTable;
import org.dreambot.cache.fs.runescape.data.CacheIndex;
import org.dreambot.cache.fs.runescape.data.ConfigArchive;
import org.dreambot.cache.game.impl.runescape.oldschool.render.RSColor;
import org.dreambot.cache.io.ByteBufferUtils;
import org.dreambot.cache.tools.CacheManager;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RSFloor {
    public static final RSFloor[] UNDERLAY_CACHE = new RSFloor[174];
    public static final RSFloor[] OVERLAY_CACHE = new RSFloor[174];

    static {
        try {
            Container tableContainer = Container.decode(CacheManager.get().getStore().read(255, 2));
            ReferenceTable table = ReferenceTable.decode(tableContainer.getData());
            Archive overlayArchive = Archive.decode(CacheManager.get().read(CacheIndex.CONFIGS.getID(),
                    ConfigArchive.OVERLAY.getID()).getData(), table.getEntry(ConfigArchive.OVERLAY.getID()).size());
            Archive underlayArchive = Archive.decode(CacheManager.get().read(CacheIndex.CONFIGS.getID(),
                    ConfigArchive.UNDERLAY.getID()).getData(), table.getEntry(ConfigArchive.UNDERLAY.getID()).size());

            int underlayID = 0;
            int overlayID = 0;
            for (int floorID = 0; floorID < UNDERLAY_CACHE.length; floorID++) {
                ReferenceTable.ChildEntry underlayEntry = table.getEntry(ConfigArchive.UNDERLAY.getID()).getEntry(floorID);
                ReferenceTable.ChildEntry overlayEntry = table.getEntry(ConfigArchive.OVERLAY.getID()).getEntry(floorID);
                if (underlayEntry != null) {
                    UNDERLAY_CACHE[floorID] = new RSFloor(underlayArchive.getEntry(underlayID++));
                }
                if (overlayEntry != null) {
                    OVERLAY_CACHE[floorID] = new RSFloor(overlayArchive.getEntry(overlayID++));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public int texture = -1;
    public boolean occlude = true;

    public RSColor secondColor = null;
    public RSColor color = null;

    public RSFloor(ByteBuffer buffer) {
        this.read(buffer);
    }


    private void read(ByteBuffer buffer) {
        int opcode;
        while ((opcode = buffer.get() & 0xFF) != 0) {
            if (opcode == 1) {
                this.color = new RSColor(ByteBufferUtils.getMedium(buffer));
            } else if (2 == opcode) {
                this.texture = buffer.get() & 0xFF;
            } else if (5 == opcode) {
                this.occlude = false;
            } else if (7 == opcode) {
                this.secondColor = new RSColor(ByteBufferUtils.getMedium(buffer));
            }
        }
    }


}
