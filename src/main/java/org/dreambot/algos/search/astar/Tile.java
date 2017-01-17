package org.dreambot.algos.search.astar;


import java.awt.*;

/**
 * @author Notorious
 * 7/28/2014
 */
public class Tile {

    private int x;
    private int z;
    private int y;

    public Tile() {
        //Default
    }

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.z = 0;
    }

    public Tile(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Translates this Tile's location by the specified amount and returns
     * then this Tile with the updated location.
     *
     * @param tile the tile which to translate.
     * @return this tile with the translated location.
     */
    public Tile translate(Tile tile) {
        return translate(tile.x, tile.y);
    }

    /**
     * Translates this Tile's location by the specified amount and returns
     * then this Tile with the updated location.
     *
     * @param x    x by which to translate.
     * @param y    y by which to translate.
     * @return this tile with the translated location.
     */
    public Tile translate(int x, int y) {
        setX(getX() + x);
        setY(getY() + y);
        return this;
    }

    /**
     * Gets the X coordinate of the Tile
     * @return X coordinate of Tile
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the X coordinate for a Tile
     * @param x X coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the Y coordinate of the Tile
     * @return Y coordinate of the tile
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the Y coordinate of the Tile
     * @param y Y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }


    /**
     * Gets the Z coordinate of the tile
     * @return Z coordinate
     */
    public int getZ() {
        return z;
    }

    /**
     * Sets the Z coordinate of the tile
     * @param z Z coordinate
     */
    public void setZ(int z) {
        this.z = z;
    }


    public Tile clone(){
        return new Tile(getX(), getY(), getZ());
    }


    public int hashCode() {
        return (this.x * this.y) >> 7;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile other = (Tile) obj;
            return other.getX() == getX()
                    && other.getY() == getY()
                    && other.getZ() == getZ();
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}
