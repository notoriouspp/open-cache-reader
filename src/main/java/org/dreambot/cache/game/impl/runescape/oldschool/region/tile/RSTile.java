package org.dreambot.cache.game.impl.runescape.oldschool.region.tile;

import org.dreambot.cache.game.impl.runescape.oldschool.region.object.RSObject;

import java.util.ArrayList;

/**
 * Created by Robert.
 * Time :   06:05.
 */
public class RSTile {
    public final ArrayList<RSObject> objects = new ArrayList<>();
    public final int underlay;
    public final int overlay;
    public final int flag;
    public final int x;
    public final int y;
    public final int z;

    /**
     * The RSTile constructor
     *
     * @param underlay - the tile's underlay flo id.
     * @param overlay  - the tile's overlay flo id.
     * @param flag     - the tile's flag.
     * @param x        - the tile's block x position.
     * @param y        - the tile's block y position.
     * @param z-       the tile's plane.
     */
    public RSTile(int underlay, int overlay, int flag, int x, int y, int z) {
        this.underlay = underlay;
        this.overlay = overlay;
        this.flag = flag;
        this.x = x;
        this.y = y;
        this.z = z;
    }


}
