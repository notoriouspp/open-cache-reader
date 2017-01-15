package org.dreambot.algos.search;

import org.dreambot.cache.game.impl.runescape.oldschool.region.CollisionMap;
import org.dreambot.cache.game.impl.runescape.oldschool.region.Region;
import org.dreambot.cache.tools.TileFlags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NotoSearch {

    public static List<SearchNode> generate(Region region, int step) {
        List<SearchNode> nodes = new ArrayList<>();

        return nodes;
    }

    public static List<SearchNode> generate(CollisionMap map, int step) {
        SearchNode[][] nodes = getRawNodeMap(map);
        for(int x = 0; x < map.clipWidth; x += step){
            for(int y = 0; y < map.clipWidth; y += step){
                if(x > step){
                    int x_ = x - step;
                    translate(map, nodes, x, y, x_, 0, TileFlags.WALL_WEST);
                }
                if(x < map.clipWidth - step){
                    int x_ = x + step;
                    translate(map, nodes, x, y, x_, 0, TileFlags.WALL_EAST);
                }
                if(y > step){
                    int y_ = y - step;
                    translate(map, nodes, x, y, 0, y_, TileFlags.WALL_NORTH);
                }
                if(y < map.clipHeight - step){
                    int y_ = y + step;
                    translate(map, nodes, x, y, 0, y_, TileFlags.WALL_SOUTH);
                }
            }
        }
        return Stream.of(nodes).flatMap(Stream::of).collect(Collectors.toList());
    }

    private static void translate(CollisionMap map, SearchNode[][] nodes, int x, int y, int x_, int y_, int flag) {
        if((map.clipData[x_][y_] & flag) != 0){
            SearchNode root = nodes[x][y];
            SearchNode translated = nodes[x_][y_];
            if(!root.children.contains(translated) && !translated.children.contains(root)) {
                translated.children.add(root);
                root.children.add(translated);
            }
        }
    }

    private static SearchNode[][] getRawNodeMap(CollisionMap map) {
        SearchNode[][] nodes = new SearchNode[map.clipWidth][map.clipHeight];
        for(int x = 0; x < map.clipWidth; x++){
            for(int y = 0; y < map.clipHeight; y++){
                nodes[y][x] = new SearchNode(x, y);
            }
        }
        return nodes;
    }

    private int x, y;

    NotoSearch(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void move(Direction4 direction) {
        this.x += direction.getX();
        this.y += direction.getY();
    }

    void report() {
    System.out.println("Location: " + x + ", " + y); 
    }

    public static class SearchNode {

        public final List<SearchNode> children = new ArrayList<>();

        public int x, y;

        public SearchNode(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
} 