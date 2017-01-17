package org.dreambot.algos.search.astar;

import org.dreambot.cache.game.impl.runescape.oldschool.region.CollisionMap;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static org.dreambot.util.Constants.BLOCK_SIZE;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : Notorious
 * @version : 0.0.1
 * @since : 7/2/2015
 * Time : 3:50 PM
 */
public class CollisionPathFinder {

    public static final int MAX_DEPTH = 1000;
    private CollisionMap map;
    private TileNode[][] tiles;

    public CollisionPathFinder(CollisionMap map, int plane) {
        this.map = map;
        tiles = new TileNode[BLOCK_SIZE][BLOCK_SIZE];
        for(int y = 0; y < BLOCK_SIZE; y++){
            for(int x = 0; x < BLOCK_SIZE; x++){
                TileNode tileNode = new TileNode(x, y, plane, map.clipData[x][y]);
                tiles[x][y] = tileNode;
            }
        }
    }

    public void construct () {
        for(int y = 0; y < BLOCK_SIZE; y++){
            for(int x = 0; x < BLOCK_SIZE; x++){
                TileNode tile = tiles[x][y];
                if(map.isWalkable(x, y, x + 1, y)){
                    tile.add(tiles[x + 1][y]);
                }
                if(map.isWalkable(x, y, x, y + 1)){
                    tile.add(tiles[x][y + 1]);
                }
                if(map.isWalkable(x, y, x - 1, y)){
                    tile.add(tiles[x - 1][y]);
                }
                if(map.isWalkable(x, y, x, y - 1)){
                    tile.add(tiles[x][y - 1]);
                }
            }
        }
    }

    public boolean canReach(int sX, int sY, int dX, int dY, boolean finishOnPoint) {
        List<PathNode> open = new ArrayList<>();
        List<PathNode> closed = new ArrayList<>();
        if(sX >= 0 && sX < BLOCK_SIZE && sY >= 0 && sY < BLOCK_SIZE){
            if(dX >= 0 && dX < BLOCK_SIZE && dY >= 0 && dY < BLOCK_SIZE){
                open.add(new PathNode(tiles[sX][sY], tiles[sX][sY], tiles[dX][dY]));
                int depth = 0;
                while(!open.isEmpty() && depth++ < MAX_DEPTH){
                    Collections.sort(open);
                    PathNode current = open.get(0);
                    if (current.getX() == dX && current.getX() == dY || (current.getX() + 1 == dX && current.getY() == dY && !finishOnPoint)
                            || (current.getX() - 1 == dX && current.getY() == dY && !finishOnPoint) || (current.getX() == dX && current.getY() + 1 == dY && !finishOnPoint)
                            || (current.getX() == dX && current.getY() - 1 == dY && !finishOnPoint)) {
                        return true;
                    }
                    List<TileNode> nodes =  tiles[current.getX()][current.getY()].getNeighbors();
                    if(!nodes.isEmpty()){
                        nodes.forEach(n -> {
                            PathNode node = new PathNode(tiles[n.getX()][n.getY()], tiles[sX][sY], tiles[dX][dY]);
                            if(!open.contains(node) && !closed.contains(node)) {
                                node.setOwner(current);
                                open.add(node);
                            }
                        });
                    }
                    open.remove(current);
                    closed.add(current);
                }
            }
        }
        return false;
    }

    private Tile[] retracePath(PathNode current) {
        Point base = map.getBlock().getGameWorldCoordinate();
        ArrayList<Tile> points = new ArrayList<>();
        points.add(current.toTile(base));
        while (current.getOwner() != null) {
            current = current.getOwner();
            points.add(new Tile(current.getX() + base.x, current.getY() + base.y, map.z));
        }
        return points.toArray(new Tile[points.size()]);
    }

    private static Tile[] reverseArray(Tile[] arr) {
        if (arr == null) {
            return null;
        }
        for (int i = 0; i < arr.length / 2; i++) {
            Tile temp = arr[i];
            arr[i] = arr[arr.length - i - 1];
            arr[arr.length - i - 1] = temp;
        }
        return arr;
    }
}
