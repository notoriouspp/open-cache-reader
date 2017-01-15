package org.dreambot.algos.search.astar;

import notoscripts.scripts.pestcontrol.wrapper.IslandTile;
import org.dreambot.api.Client;
import org.dreambot.api.methods.map.Tile;

/**
 * Created with IntelliJ IDEA.
 *
 * @author : Notorious
 * @version : 0.0.1
 * @since : 7/2/2015
 * Time : 3:50 PM
 */
public class IslandPathNode implements Comparable<IslandPathNode>{

    private IslandPathNode owner;
    private IslandTile current;
    private IslandTile start;
    private IslandTile destination;


    public IslandPathNode(IslandTile current, IslandTile start, IslandTile destination) {
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

    public IslandPathNode getOwner() {
        return owner;
    }

    public Tile toTile (Client client){
        return new Tile(getX() + client.getBaseX(), getY() + client.getBaseY());
    }

    public void setOwner(IslandPathNode owner) {
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
        return obj instanceof IslandPathNode
                && ((IslandPathNode) obj).getX() == getX()
                && ((IslandPathNode) obj).getY() == getY();
    }

    @Override
    public int compareTo(IslandPathNode o) {
        return (int)((getCost() * 100) - (o.getCost() * 100));
    }
}
