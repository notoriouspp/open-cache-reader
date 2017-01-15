package org.dreambot.cache.tools;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 1/14/2017
 */
public interface TileFlags {
    int NULL = 0x0;

    int WALL_NORTHWEST = 0x1;
    int WALL_NORTH = 0x2;
    int WALL_NORTHEAST = 0x4;
    int WALL_EAST = 0x8;
    int WALL_SOUTHEAST = 0x10;
    int WALL_SOUTH = 0x20;
    int WALL_SOUTHWEST = 0x40;
    int WALL_WEST = 0x80;
    int WALL_MASK = (
            TileFlags.WALL_NORTH | TileFlags.WALL_EAST
            | TileFlags.WALL_SOUTH | TileFlags.WALL_WEST
            | TileFlags.WALL_NORTHWEST | TileFlags.WALL_NORTHEAST
            | TileFlags.WALL_SOUTHEAST | TileFlags.WALL_SOUTHWEST
    );
    int WALL_BLOCK_MASK = (
            TileFlags.WALL_BLOCK_NORTH | TileFlags.WALL_BLOCK_EAST
                    | TileFlags.WALL_BLOCK_SOUTH | TileFlags.WALL_BLOCK_WEST
                    | TileFlags.WALL_BLOCK_NORTHWEST | TileFlags.WALL_BLOCK_NORTHEAST
                    | TileFlags.WALL_BLOCK_SOUTHEAST | TileFlags.WALL_BLOCK_SOUTHWEST
    );
    int OBJECT_MASK = 0x100 | 0x20000 | 0x40000;

    int OBJECT_TILE = 0x100;
    int WALL_BLOCK_NORTHWEST = 0x200;
    int WALL_BLOCK_NORTH = 0x400;
    int WALL_BLOCK_NORTHEAST = 0x800;
    int WALL_BLOCK_EAST = 0x1000;
    int WALL_BLOCK_SOUTHEAST = 0x2000;
    int WALL_BLOCK_SOUTH = 0x4000;
    int WALL_BLOCK_SOUTHWEST = 0x8000;
    int WALL_BLOCK_WEST = 0x10000;
    int OBJECT_BLOCK = 0x20000;
    int DECORATION_BLOCK = 0x40000;

    int WALL_ALLOW_RANGE_NORTHWEST = 0x400000;
    int WALL_ALLOW_RANGE_NORTH = 0x800000;
    int WALL_ALLOW_RANGE_NORTHEAST = 0x1000000;
    int WALL_ALLOW_RANGE_EAST = 0x2000000;
    int WALL_ALLOW_RANGE_SOUTHEAST = 0x4000000;
    int WALL_ALLOW_RANGE_SOUTH = 0x8000000;
    int WALL_ALLOW_RANGE_SOUTHWEST = 0x10000000;
    int WALL_ALLOW_RANGE_WEST = 0x20000000;
    int OBJECT_ALLOW_RANGE = 0x40000000;
    int WATER = 0x1280100;

    int UNLOADED = 0xFFFFFF;
    int UNLOADED_ = 0x1000000;
}
