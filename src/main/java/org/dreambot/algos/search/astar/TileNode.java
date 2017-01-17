package org.dreambot.algos.search.astar;

import org.dreambot.algos.search.Direction4;
import org.dreambot.cache.game.impl.runescape.oldschool.region.RSRegionBlock;
import org.dreambot.cache.game.impl.runescape.oldschool.region.Region;
import org.dreambot.cache.game.impl.runescape.oldschool.region.RegionPathFinder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.dreambot.util.Constants.BLOCK_SIZE;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : Notorious
 * @version : 0.0.1
 * @since : 7/2/2015
 * Time : 3:50 PM
 */
public class TileNode {

    private int x;
    private int y;
    private int z;
    private int flag;

    private List<TileNode> neighbors;

    public TileNode(int x, int y, int z, int flag) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.flag = flag;
        neighbors = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getFlag() {
        return flag;
    }

    public Tile toWorldTile (RSRegionBlock block){
        Point gameWorldCoordinate = block.getGameWorldCoordinate();
        return new Tile(x + gameWorldCoordinate.x, y + gameWorldCoordinate.y);
    }

    public boolean hasNeighbors () {
        return !neighbors.isEmpty();
    }

    public void add(TileNode... tiles){
        if(tiles != null) {
            Collections.addAll(neighbors, tiles);
        }
    }



    public List<TileNode> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return new Point(x, y).toString();
    }

    public void connect(TileNode[][] map, RegionPathFinder finder, int step) {
        for(Direction4 direction : Direction4.values()) {
            translate(direction, map, finder, step);
        }
    }

    private void translate(Direction4 direction, TileNode[][] map, RegionPathFinder finder, int step) {
        int x = getX() + direction.getX() * step;
        int y = getY() + direction.getY() * step;
        int length = map.length;
        if(x >= 0 && y >= 0 && x < length && y < length){
            TileNode tileNode = map[x][y];
            if(tileNode != null && !tileNode.getNeighbors().contains(this) && !getNeighbors().contains(tileNode)){
                boolean canReach = finder.canReach(getX(), getY(), x, y);
                if(canReach){
                    getNeighbors().add(tileNode);
                    tileNode.getNeighbors().add(this);
                }
            }
        }
    }
}
