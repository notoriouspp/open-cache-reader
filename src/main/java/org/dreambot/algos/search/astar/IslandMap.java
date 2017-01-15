package org.dreambot.algos.search.astar;

import notoscripts.scripts.pestcontrol.wrapper.path.IslandPathNode;
import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.path.impl.LocalPath;
import org.dreambot.api.wrappers.map.impl.CollisionMap;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : Notorious
 * @version : 0.0.1
 * @since : 7/2/2015
 * Time : 3:50 PM
 */
public class IslandMap {

    public static final int MAX_DEPTH = 1000;
    private Client client;
    private CollisionMap map;
    private IslandTile[][] tiles;

    public IslandMap (Client client) {
        this.client = client;
        this.map = client.getCollisionMaps()[client.getPlane()];
        tiles = new IslandTile[104][104];
        for(int y = 0; y < 104; y++){
            for(int x = 0; x < 104; x++){
                tiles[x][y] = new IslandTile(x, y, map.getFlag(x, y));
            }
        }
    }

    public void construct () {
        for(int y = 0; y < 104; y++){
            for(int x = 0; x < 104; x++){
                IslandTile tile = tiles[x][y];
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

    public LocalPath<Tile> findPath (Tile destination, boolean finishOnPoint) {
        return findPath(client.getLocalPlayer().getX() - client.getBaseX(),
                client.getLocalPlayer().getY() - client.getBaseY(),
                destination.getX() - client.getBaseX(),
                destination.getY() - client.getBaseY(),
                finishOnPoint);
    }

    public LocalPath<Tile> findPath (Tile start, Tile destination, boolean finishOnPoint) {
        return findPath(start.getX() - client.getBaseX(), start.getY() - client.getBaseY(),
                destination.getX() - client.getBaseX(), destination.getY() - client.getBaseY(), finishOnPoint);
    }

    public LocalPath<Tile> findPath (int sX, int sY, int dX, int dY, boolean finishOnPoint) {
        List<IslandPathNode> open = new ArrayList<>();
        List<IslandPathNode> closed = new ArrayList<>();
        if(sX >= 0 && sX < 104 && sY >= 0 && sY < 104){
            if(dX >= 0 && dX < 104 && dY >= 0 && dY < 104){
                open.add(new IslandPathNode(tiles[sX][sY], tiles[sX][sY], tiles[dX][dY]));
                int depth = 0;
                while(!open.isEmpty() && depth++ < MAX_DEPTH){
                    Collections.sort(open);
                    IslandPathNode current = open.get(0);
                    if (current.getX() == dX && current.getX() == dY || (current.getX() + 1 == dX && current.getY() == dY && !finishOnPoint)
                            || (current.getX() - 1 == dX && current.getY() == dY && !finishOnPoint) || (current.getX() == dX && current.getY() + 1 == dY && !finishOnPoint)
                            || (current.getX() == dX && current.getY() - 1 == dY && !finishOnPoint)) {
                        LocalPath<Tile> path = new LocalPath<>(client.getMethodContext());
                        Tile[] raw = reverseArray(retracePath(current));
                        if(raw != null) {
                            path.addAll(raw);
                        }
                        return path;
                    }
                    List<IslandTile> nodes =  tiles[current.getX()][current.getY()].getNeighbors();
                    if(!nodes.isEmpty()){
                        nodes.forEach(n -> {
                            IslandPathNode node = new IslandPathNode(tiles[n.getX()][n.getY()], tiles[sX][sY], tiles[dX][dY]);
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
        return null;
    }

    private Tile[] retracePath(IslandPathNode current) {
        int baseX = client.getBaseX();
        int baseY = client.getBaseY();
        ArrayList<Tile> points = new ArrayList<>();
        points.add(current.toTile(client));
        while (current.getOwner() != null) {
            current = current.getOwner();
            points.add(new Tile(current.getX() + baseX, current.getY() + baseY, client.getPlane()));
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

    public void paint (Graphics2D g) {
        for(int y = 0; y < 104; y++) {
            for (int x = 0; x < 104; x++) {
                IslandTile tile = tiles[x][y];
                if(tile.hasNeighbors()) {
                    Point point = client.getViewportTools().tileToScreen(tile.toTile(client));
                    if(point.x > -1 && point.y > -1) {
                        for (IslandTile t : tile.getNeighbors()) {
                            Point point_ = client.getViewportTools().tileToScreen(t.toTile(client));
                            if (point_.x > -1 && point_.y > -1) {
                                g.drawLine(point.x, point.y, point_.x, point_.y);
                            }
                        }
                    }
                }
            }
        }
    }
}
