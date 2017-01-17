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
        return current.getX();
    }

    public int getY() {
        return current.getY();
    }

    public PathNode getOwner() {
        return owner;
    }

    public Tile toTile (Point base){
        return new Tile(getX() + base.x, getY() + base.y);
    }

    public void setOwner(PathNode owner) {
        this.owner = owner;
    }

    public float getCost() {
        float x = getX() - destination.getX();
        float y = getY() - destination.getY();
        float heuristic = (float)(Math.sqrt((x * x) + (y * y)));
        float dx2 = start.getX() - destination.getX();
        float dy2 = start.getY() - destination.getY();
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
        return (int)((getCost() * 100) - (o.getCost() * 100));
    }
}
