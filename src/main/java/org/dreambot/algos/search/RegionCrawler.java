package org.dreambot.algos.search;

import org.dreambot.algos.ConcaveHull;
import org.dreambot.algos.convexhall.ConvexHall;
import org.dreambot.algos.convexhall.Point2D;
import org.dreambot.algos.search.astar.TileNode;
import org.dreambot.algos.search.peucker.RamerDouglasPeucker;
import org.dreambot.cache.game.impl.runescape.oldschool.region.RCollisionMap;
import org.dreambot.cache.game.impl.runescape.oldschool.region.Region;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.dreambot.util.Constants.BLOCK_SIZE;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 1/17/2017
 */
public class RegionCrawler {

    public static List<TileNode> generate(Region region, int plane, int step) {
        return new RegionCrawler(region).getConnectedNodeMap(plane, step);
    }

    private final Region region;

    private RegionCrawler(Region region) {
        this.region = region;
    }

    //TODO Make this work
    private List<TileNode> getConnectedNodeMap(int plane, int step) {
        TileNode[][] nodes = new TileNode[region.sizeX * BLOCK_SIZE][region.sizeY * BLOCK_SIZE];
        int width = region.tilesDimension.width;
        int height = region.tilesDimension.height;
        RCollisionMap collisionMap = region.collisionMaps[0];
        for(int x = 0; x < width; x += step){
            for(int y = 0; y < height; y += step) {
                TileNode tileNode = new TileNode(x, y, plane, collisionMap.clipData[x][y], x, y);
                    if(RCollisionMap.isBlocked(tileNode.getFlag())) {
                    int denominator = 1;
                    find:
                    {
                        for (int y_ = -(step / denominator); y_ < (step / denominator); y_++) {
                            int tY = y + y_;
                            if (tY >= 0 && tY < collisionMap.clipHeight) {
                                int flag = collisionMap.clipData[x][tY];
                                if (!RCollisionMap.isBlocked(flag)) {
                                    tileNode.setY(tY);
                                    tileNode.setFlag(flag);
                                    break find;
                                }
                            }
                        }
                        for (int x_ = -(step / denominator); x_ < (step / denominator); x_++) {
                            int tX = x + x_;
                            if (tX >= 0 && tX < collisionMap.clipWidth) {
                                int flag = collisionMap.clipData[tX][y];
                                if (!RCollisionMap.isBlocked(flag)) {
                                    tileNode.setX(tX);
                                    tileNode.setFlag(flag);
                                    break find;
                                }
                            }
                        }

                    }
                }
                nodes[x][y] = tileNode;
            }
        }

        List<TileNode> collect = Arrays.stream(nodes)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .map(n -> n.connect(nodes, region.pathFinders[plane], step))
                .collect(Collectors.toList());
        ArrayList<TileNode> temp = new ArrayList<>(collect);
        collect.removeIf(n -> n.getNeighbors().isEmpty());
        temp.forEach(t -> {
            t.getNeighbors().removeIf(t::equals);
            List<TileNode> neighbors = t.getNeighbors();
            neighbors.forEach(n -> {
                if(t.distance(n) < 1f){
                    t.getNeighbors().addAll(n.getNeighbors().stream().filter(o -> !o.equals(t) && !t.getNeighbors().contains(o)).collect(Collectors.toList()));
                    collect.remove(n);
                }
            });
            neighbors = new ArrayList<>(t.getNeighbors());
            t.getNeighbors().addAll(RamerDouglasPeucker.process(neighbors, step * 1.5D));
        });
        return collect;
    }

    private boolean translate(RCollisionMap collisionMap, TileNode tileNode, int x, int y, int oX, int oY) {
        int tX = x + x;
        int tY = y + oY;
        if(tX >= 0 && tY >= 0 && tX < collisionMap.clipWidth && tY < collisionMap.clipHeight) {
            int flag = collisionMap.clipData[tX][tY];
            if (!RCollisionMap.isBlocked(flag)) {
                tileNode.setX(tX);
                tileNode.setY(tY);
                tileNode.setFlag(flag);
                return true;
            }
        }
        return false;
    }
}
