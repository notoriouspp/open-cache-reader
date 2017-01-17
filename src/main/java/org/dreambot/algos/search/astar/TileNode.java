package org.dreambot.algos.search.astar;

import org.dreambot.cache.game.impl.runescape.oldschool.region.RSRegionBlock;
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
public class TileNode extends Point {

    private final int archive;
    private final int index;
    private int z;
    private int flag;

    private List<TileNode> neighbors;

    public TileNode(int x, int y, int z, int flag, int archive, int index) {
        super(x, y);
        this.z = z;
        this.flag = flag;
        this.archive = archive;
        this.index = index;
        neighbors = new ArrayList<>();
    }

    public double distance(TileNode node) {
        return new Point(x, y).distance(node.getX(), node.getY());
    }

    public int getZ() {
        return z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getArchive() {
        return archive;
    }

    public int getIndex() {
        return index;
    }

    public void setFlag(int flag) {
        this.flag = flag;
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

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TileNode){
            return x == ((TileNode) obj).x
                    && y == ((TileNode) obj).y;
        }
        return super.equals(obj);
    }

    public List<TileNode> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        return new Point(x, y).toString();
    }

    public TileNode connect(TileNode[][] map, RegionPathFinder finder, int step) {
        for(int x = -step; x <= step; x += step){
            translate(x, 0, map, finder);
        }
        for(int y = -step; y <= step; y += step){
            translate(0, y, map, finder);
        }
        return this;
    }

    private void translate(int offsetX, int offsetY, TileNode[][] map, RegionPathFinder finder) {
        int x = archive + offsetX;
        int y = index + offsetY;
        int length = map.length;
        if(x >= 0 && y >= 0 && x < length && y < length){
            TileNode tileNode = map[x][y];
            boolean canReach = finder.canReach(this.x, this.y, x, y);
            if(canReach){
                if(!getNeighbors().contains(tileNode)) {
                    getNeighbors().add(tileNode);
                }
                if(!tileNode.getNeighbors().contains(this)) {
                    tileNode.getNeighbors().add(this);
                }
            }
        }
    }
}
