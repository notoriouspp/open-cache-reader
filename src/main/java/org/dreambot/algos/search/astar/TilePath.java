package org.dreambot.algos.search.astar;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : Notorious
 * @version : 0.0.1
 * @since : 7/2/2015
 * Time : 3:50 PM
 */
public class TilePath {

    private List<TileNode> tiles;

    public TilePath(List<TileNode> tiles) {
        this.tiles = tiles;
    }

    public void add(TileNode... tiles){
        if(tiles != null) {
            Collections.addAll(this.tiles, tiles);
        }
    }

    public boolean isEmpty () {
        return tiles.isEmpty();
    }

    public List<TileNode> getTiles() {
        return tiles;
    }
}
