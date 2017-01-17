package org.dreambot.algos.search;

import org.dreambot.algos.search.astar.TileNode;
import org.dreambot.cache.game.impl.runescape.oldschool.region.Region;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
        for(int x = 0; x < width; x += step){
            for(int y = 0; y < height; y += step) {
                nodes[x][y] = new TileNode(x, y, plane, region.collisionMaps[0].clipData[x][y]);
            }
        }
        return Arrays.stream(nodes)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .map(n -> n.connect(nodes, region.pathFinders[plane], step))
                .collect(Collectors.toList());
    }
}
