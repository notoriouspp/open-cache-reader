package org.dreambot.cache.game.impl.runescape.oldschool.region;

import org.dreambot.algos.search.astar.PathNode;
import org.dreambot.algos.search.astar.Tile;
import org.dreambot.algos.search.astar.TileNode;

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
public class RegionPathFinder {

    public static final int MAX_DEPTH = 32;
    private final int clipWidth;
    private final int clipHeight;
    private final Region region;
    private final int z;
    private RCollisionMap map;
    private TileNode[][] tiles;

    public RegionPathFinder(Region region, RCollisionMap map, int plane) {
        this.map = map;
        this.region = region;
        clipWidth = this.region.sizeX * BLOCK_SIZE;
        clipHeight = region.sizeY * BLOCK_SIZE;
        tiles = new TileNode[clipWidth][clipHeight];
        z = plane;
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[x].length; y++){
                TileNode tileNode = new TileNode(x, y, z, map.clipData[x][y], x, y);
                tiles[x][y] = tileNode;
            }
        }
    }

    public RegionPathFinder construct () {
        for(int x = 0; x < clipWidth; x++){
            for(int y = 0; y < clipHeight; y++){
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
        int scale = 4;
        int sideX = (region.sizeX * BLOCK_SIZE) * scale;
        int sideY = (region.sizeY * BLOCK_SIZE) * scale;
        BufferedImage image = new BufferedImage(sideX, sideY, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.CYAN);
        Stream.of(tiles).flatMap(Stream::of).forEach(t -> {
            t.getNeighbors().stream().filter(Objects::nonNull).forEach(n -> {
                graphics.drawLine(t.x * scale, sideY - (t.y * scale), n.x * scale, sideY - (n.y * scale));
            });
        });
        graphics.setColor(Color.GREEN);
        int x1 = 3;
        int y1 = 78;
        int x2 = 12;
        int y2 = 78;
        graphics.fill(new Rectangle(x1 * scale, y1 * scale, scale, scale));
        graphics.setColor(Color.ORANGE);
        graphics.fill(new Rectangle(x2 * scale, y2 * scale, scale, scale));
        System.out.println(region.isClipped(x1, y2, 0));
        System.out.println(region.isClipped(x2, y2, 0));
        List<Tile> path = findPath(x1, y1, x2, y2);
        System.out.println(path);
        if(path != null) {
            graphics.setStroke(new BasicStroke(2));
            graphics.setColor(Color.GREEN);
            path.forEach(p ->  graphics.fill(new Rectangle(p.getX() * scale, p.getY() * scale, scale, scale)));
        }
        File output = new File("regions\\region" + map.z  + "reagionCollisionTest.png");
        if(!output.exists()){
            output.mkdirs();
        }
        try {
            ImageIO.write(image, "png", output);
        } catch (Exception e){
            e.printStackTrace();
        }
        return this;
    }

    public List<Tile> findPath(int sX, int sY, int dX, int dY) {
        List<PathNode> open = new ArrayList<>();
        List<PathNode> closed = new ArrayList<>();
        if(sX >= 0 && sX < clipWidth && sY >= 0 && sY < clipHeight){
            if(dX >= 0 && dX < clipWidth && dY >= 0 && dY < clipHeight){
                TileNode start = tiles[sX][sY];
                TileNode destination = tiles[dX][dY];
                open.add(new PathNode(start, start, destination));
                int depth = 0;
                while(!open.isEmpty() && depth++ < MAX_DEPTH){
                    Collections.sort(open);
                    PathNode current = open.get(0);
                    if (current.getX() == destination.getX() && current.getY() == destination.getY()) {
                        Tile[] raw = reverseArray(retracePath(current));
                        List<Tile> list = new ArrayList<>();
                        if(raw != null) {
                            Collections.addAll(list, raw);
                        }
                        return list;
                    }
                    List<TileNode> nodes =  tiles[current.getX()][current.getY()].getNeighbors();
                    if(!nodes.isEmpty()){
                        nodes.forEach(n -> {
                            PathNode node = new PathNode(tiles[n.x][n.y], start, destination);
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

    private Tile[] retracePath(PathNode current) {
        ArrayList<Tile> points = new ArrayList<>();
        points.add(current.toTile());
        while (current.getOwner() != null) {
            current = current.getOwner();
            points.add(new Tile(current.getX(), current.getY(), z));
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

    public boolean canReach(int sX, int sY, int dX, int dY) {
        List<Tile> path = findPath(sX, sY, dX, dY);
        return path != null && !path.isEmpty();
    }
}
