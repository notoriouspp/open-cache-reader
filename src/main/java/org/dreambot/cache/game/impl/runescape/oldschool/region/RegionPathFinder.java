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

    public static final int MAX_DEPTH = 1000;
    private final int clipWidth;
    private final int clipHeight;
    private final Region region;
    private RCollisionMap map;
    private TileNode[][] tiles;

    public RegionPathFinder(Region region, RCollisionMap map, int plane) {
        this.map = map;
        this.region = region;
        clipWidth = this.region.sizeX * BLOCK_SIZE;
        clipHeight = region.sizeY * BLOCK_SIZE;
        tiles = new TileNode[clipWidth][clipHeight];
        for(int x = 0; x < tiles.length; x++){
            for(int y = 0; y < tiles[x].length; y++){
                TileNode tileNode = new TileNode(x, y, plane, map.clipData[x][y]);
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
                graphics.drawLine(t.getX() * scale, sideY - (t.getY() * scale), n.getX() * scale, sideY - (n.getY() * scale));
            });
        });
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

    public boolean canReach(int sX, int sY, int dX, int dY) {
        List<PathNode> open = new ArrayList<>();
        List<PathNode> closed = new ArrayList<>();
        if(sX >= 0 && sX < BLOCK_SIZE && sY >= 0 && sY < BLOCK_SIZE){
            if(dX >= 0 && dX < BLOCK_SIZE && dY >= 0 && dY < BLOCK_SIZE){
                TileNode start = tiles[sX][sY];
                TileNode destination = tiles[dX][dY];
                open.add(new PathNode(start, start, destination));
                int depth = 0;
                while(!open.isEmpty() && depth++ < MAX_DEPTH){
                    Collections.sort(open);
                    PathNode current = open.get(0);
                    if (current.getX() == dX && current.getX() == dY) {
                        return true;
                    }
                    List<TileNode> nodes =  tiles[current.getX()][current.getY()].getNeighbors();
                    if(!nodes.isEmpty()){
                        nodes.forEach(n -> {
                            PathNode node = new PathNode(tiles[n.getX()][n.getY()], start, destination);
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
}
