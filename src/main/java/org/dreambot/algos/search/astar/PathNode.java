package org.dreambot.algos.search.astar;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : Notorious
 * @version : 0.0.1
 * @since : 7/2/2015
 * Time : 3:50 PM
 */
public class PathNode implements Comparable<PathNode>{

    private PathNode owner;
    private TileNode current;
    private TileNode start;
    private TileNode destination;


    public PathNode(TileNode current, TileNode start, TileNode destination) {
        this.current = current;
        this.start = start;
        this.destination = destination;
    }

    public int getX() {
        return current.x;
    }

    public int getY() {
        return current.y;
    }

    public PathNode getOwner() {
        return owner;
    }

    public Tile toTile (Point base){
        return new Tile(getX() + base.x, getY() + base.y);
    }

    public Tile toTile (){
        return new Tile(getX(), getY());
    }

    public void setOwner(PathNode owner) {
        this.owner = owner;
    }

    public float getCost() {
        float x = getX() - destination.x;
        float y = getY() - destination.y;
        float heuristic = (float)(Math.sqrt((x * x) + (y * y)));
        float dx2 = start.x - destination.x;
        float dy2 = start.y - destination.y;
        float cross = (Math.abs((x * dy2) - (dx2 * y)));
        return heuristic + (cross * 0.001F);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PathNode
                && ((PathNode) obj).getX() == getX()
                && ((PathNode) obj).getY() == getY();
    }

    @Override
    public String toString() {
        return new Point(getX(), getY()).toString();
    }

    @Override
    public int compareTo(PathNode o) {
        float f = getCost();
        float of = o.getCost();
        return f < of ? -1 : f > of ? 1 : 0;
    }
}
