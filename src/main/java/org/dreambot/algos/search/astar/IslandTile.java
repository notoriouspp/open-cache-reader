package org.dreambot.algos.search.astar;

import org.dreambot.cache.game.impl.runescape.oldschool.region.RSRegionBlock;

import java.util.ArrayList;
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
public class IslandTile {

    private int x;
    private int y;
    private int flag;

    private List<IslandTile> neighbors;

    public IslandTile(int x, int y, int flag) {
        this.x = x;
        this.y = y;
        this.flag = flag;
        neighbors = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    public int getFlag() {
        return flag;
    }

    public Tile toTile (RSRegionBlock regionBlock){
        return new Tile(x + client.getBaseX(), y + client.getBaseY());
    }

    public boolean hasNeighbors () {
        return !neighbors.isEmpty();
    }

    public void add(IslandTile... tiles){
        if(tiles != null) {
            Collections.addAll(neighbors, tiles);
        }
    }

    public List<IslandTile> getNeighbors() {
        return neighbors;
    }
}
