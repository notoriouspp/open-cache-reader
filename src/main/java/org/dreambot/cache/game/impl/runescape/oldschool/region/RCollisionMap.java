package org.dreambot.cache.game.impl.runescape.oldschool.region;

import org.dreambot.cache.game.impl.runescape.oldschool.definition.ObjectDefinition;
import org.dreambot.cache.game.impl.runescape.oldschool.region.object.RSObject;
import org.dreambot.cache.tools.TileFlags;

import java.util.Arrays;

import static org.dreambot.cache.tools.TileFlags.*;
import static org.dreambot.cache.tools.TileFlags.WALL_NORTHEAST;
import static org.dreambot.cache.tools.TileFlags.WALL_SOUTHEAST;
import static org.dreambot.util.Constants.BLOCK_SIZE;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 1/17/2017
 */
public class RCollisionMap {

    public int setX = 0;
    public int clipHeight;
    public int clipWidth;
    public int setY = 0;
    public int z;
    public int[][] clipData;
    private Region region;

    public RCollisionMap(Region region, int z) {
        this.region = region;
        this.z = z;
        this.clipWidth = region.tilesDimension.width;
        this.clipHeight = region.tilesDimension.height;
        this.clipData = new int[this.clipWidth][this.clipHeight];
        this.reset();
    }

    public void reset() {
        for(int var2 = 0; var2 < this.clipWidth; ++var2) {
            for(int var3 = 0; var3 < this.clipHeight; ++var3) {
                if(var2 != 0 && 0 != var3 && var2 < this.clipWidth - 5 && var3 < this.clipHeight - 5) {
                    this.clipData[var2][var3] = TileFlags.UNLOADED;
                } else {
                    this.clipData[var2][var3] = TileFlags.UNLOADED_;
                }
            }
        }
    }

    public void mark(RSRegionBlock block, RSObject object) {
        if(object == null){
            return;
        }
        ObjectDefinition objectDefinition = object.getDef();
        if (object.type.ordinal() == 10 || object.type.ordinal() == 11) {
            if (objectDefinition.solid && objectDefinition.name != null)
                markSolidOccupant(block, object.x, object.y, object.width, object.height, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 22) {
            if (objectDefinition.solid && objectDefinition.actionsBoolean)
                markBlocked(object.x, object.y);
        }
        if (object.type.ordinal() >= 12) {
            if (objectDefinition.solid && objectDefinition.actionsBoolean)
                markSolidOccupant(block, object.x, object.y, object.width, object.height, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 0) {
            if (objectDefinition.solid)
                markWall(block, object.x, object.y, object.type.ordinal(), object.objectOrientation, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 1) {
            if (objectDefinition.solid)
                markWall(block, object.x, object.y, object.type.ordinal(), object.objectOrientation, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 2) {
            if (objectDefinition.solid)
                markWall(block, object.x, object.y, object.type.ordinal(), object.objectOrientation, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 3) {
            if (objectDefinition.solid)
                markWall(block, object.x, object.y, object.type.ordinal(), object.objectOrientation, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 9) {
            if (objectDefinition.solid)
                markSolidOccupant(block, object.x, object.y, object.width, object.height, objectDefinition.impenetrable);
            return;
        }
    }

    public void markWall(RSRegionBlock block, int x, int y, int type, int orientation, boolean impenetrable) {
        x += (block.blockX - region.startX) * BLOCK_SIZE; //adding block base
        y += (block.blockY - region.startY) * BLOCK_SIZE; //adding block base
        x -= this.setX;
        y -= this.setY;
        if(type == 0) {
            if(0 == orientation) {
                this.addClipLocation(x, y, WALL_WEST);
                this.addClipLocation(x - 1, y, WALL_EAST);
            }

            if(1 == orientation) {
                this.addClipLocation(x, y, WALL_NORTH);
                this.addClipLocation(x, y + 1, TileFlags.WALL_SOUTH);
            }

            if(2 == orientation) {
                this.addClipLocation(x, y, WALL_EAST);
                this.addClipLocation(1 + x, y, WALL_WEST);
            }

            if(3 == orientation) {
                this.addClipLocation(x, y, TileFlags.WALL_SOUTH);
                this.addClipLocation(x, y - 1, WALL_NORTH);
            }
        }

        if(type == 1 || 3 == type) {
            if(orientation == 0) {
                this.addClipLocation(x, y, TileFlags.WALL_NORTHWEST);
                this.addClipLocation(x - 1, 1 + y, TileFlags.WALL_SOUTHEAST);
            }

            if(orientation == 1) {
                this.addClipLocation(x, y, TileFlags.WALL_NORTHEAST);
                this.addClipLocation(1 + x, 1 + y, WALL_SOUTHWEST);
            }

            if(orientation == 2) {
                this.addClipLocation(x, y, TileFlags.WALL_SOUTHEAST);
                this.addClipLocation(1 + x, y - 1, TileFlags.WALL_NORTHWEST);
            }

            if(3 == orientation) {
                this.addClipLocation(x, y, WALL_SOUTHWEST);
                this.addClipLocation(x - 1, y - 1, TileFlags.WALL_NORTHEAST);
            }
        }

        if(type == 2) {
            if(0 == orientation) {
                this.addClipLocation(x, y, WALL_WEST | WALL_NORTH);
                this.addClipLocation(x - 1, y, WALL_EAST);
                this.addClipLocation(x, 1 + y, TileFlags.WALL_SOUTH);
            }

            if(1 == orientation) {
                this.addClipLocation(x, y, WALL_EAST | WALL_NORTH);
                this.addClipLocation(x, y + 1, TileFlags.WALL_SOUTH);
                this.addClipLocation(1 + x, y, WALL_WEST);
            }

            if(orientation == WALL_NORTH) {
                this.addClipLocation(x, y, WALL_EAST | TileFlags.WALL_SOUTH);
                this.addClipLocation(x + 1, y, WALL_WEST);
                this.addClipLocation(x, y - 1, WALL_NORTH);
            }

            if(orientation == 3) {
                this.addClipLocation(x, y, WALL_WEST | TileFlags.WALL_SOUTH);
                this.addClipLocation(x, y - 1, WALL_NORTH);
                this.addClipLocation(x - 1, y, WALL_EAST);
            }
        }

        if(impenetrable) {
            if(type == 0) {
                if(0 == orientation) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_WEST);
                    this.addClipLocation(x - 1, y, TileFlags.WALL_BLOCK_EAST);
                }

                if(1 == orientation) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_NORTH);
                    this.addClipLocation(x, y + 1, TileFlags.WALL_BLOCK_SOUTH);
                }

                if(2 == orientation) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_EAST);
                    this.addClipLocation(1 + x, y, TileFlags.WALL_BLOCK_WEST);
                }

                if(3 == orientation) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_SOUTH);
                    this.addClipLocation(x, y - 1, TileFlags.WALL_BLOCK_NORTH);
                }
            }

            if(type == 1 || 3 == type) {
                if(0 == orientation) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_NORTHWEST);
                    this.addClipLocation(x - 1, 1 + y, TileFlags.WALL_BLOCK_SOUTHEAST);
                }

                if(orientation == 1) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_NORTHEAST);
                    this.addClipLocation(1 + x, 1 + y, TileFlags.WALL_BLOCK_SOUTHWEST);
                }

                if(orientation == 2) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_SOUTHEAST);
                    this.addClipLocation(1 + x, y - 1, TileFlags.WALL_BLOCK_NORTHWEST);
                }

                if(orientation == 3) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_SOUTHWEST);
                    this.addClipLocation(x - 1, y - 1, TileFlags.WALL_BLOCK_NORTHEAST);
                }
            }

            if(type == WALL_NORTH) {
                if(0 == orientation) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_WEST | TileFlags.WALL_BLOCK_NORTH);
                    this.addClipLocation(x - 1, y, TileFlags.WALL_BLOCK_EAST);
                    this.addClipLocation(x, y + 1, TileFlags.WALL_BLOCK_SOUTH);
                }

                if(1 == orientation) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_EAST | TileFlags.WALL_BLOCK_NORTH);
                    this.addClipLocation(x, 1 + y, TileFlags.WALL_BLOCK_SOUTH);
                    this.addClipLocation(x + 1, y, TileFlags.WALL_BLOCK_WEST);
                }

                if(orientation == 2) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_EAST | TileFlags.WALL_BLOCK_SOUTH);
                    this.addClipLocation(1 + x, y, TileFlags.WALL_BLOCK_WEST);
                    this.addClipLocation(x, y - 1, TileFlags.WALL_BLOCK_NORTH);
                }

                if(orientation == 3) {
                    this.addClipLocation(x, y, TileFlags.WALL_BLOCK_WEST | TileFlags.WALL_BLOCK_SOUTH);
                    this.addClipLocation(x, y - 1, TileFlags.WALL_BLOCK_NORTH);
                    this.addClipLocation(x - 1, y, TileFlags.WALL_BLOCK_EAST);
                }
            }
        }
    }

    public void markSolidOccupant(RSRegionBlock block, int x, int y, int width, int height, boolean solid) {
        int var7 = TileFlags.OBJECT_TILE;
        if(solid) {
            var7 += TileFlags.PROJECTILE_BLOCK;
        }
        x += (block.blockX - region.startX) * BLOCK_SIZE; //adding block base
        y += (block.blockY - region.startY) * BLOCK_SIZE; //adding block base
        x -= this.setX;
        y -= this.setY;

        for(int var9 = x; var9 < width + x; ++var9) {
            if(var9 >= 0 && var9 < this.clipWidth) {
                for(int var8 = y; var8 < height + y; ++var8) {
                    if(var8 >= 0 && var8 < this.clipHeight) {
                        this.addClipLocation(var9, var8, var7);
                    }
                }
            }
        }
    }

    public void clear(int x, int y) {
        x -= this.setX;
        y -= this.setY;
        this.clipData[x][y] |= TileFlags.NULL;
    }


    public void markBlocked(int x, int y) {
        x -= this.setX;
        y -= this.setY;
        this.clipData[x][y] |= TileFlags.PROJECTILE_BLOCK;
    }

    private void addClipLocation(int x, int y, int flag) {
        if(x < 0 || x >= clipWidth || y < 0 || y >= clipHeight){
            return;
        }
        this.clipData[x][y] |= flag;
    }

    public boolean isWalkable(int x1, int y1, int x2, int y2) {
        if(!(x1 >= 0 && y1 >= 0 && x1 < clipWidth && y1 < clipHeight)){
            return false;
        }
        if(!(x2 >= 0 && y2 >= 0 && x2 < clipWidth && y2 < clipHeight)){
            return false;
        }
        boolean walkable = false;
        int here = clipData[x1][y1];
        int there = clipData[x2][y2];
        if (!isBlocked(here) && !isBlocked(there)) {
            if (x1 == x2 && y1 - 1 == y2) {
                walkable = !isCardinalDirectionBlocked(WALL_SOUTH, here, there)
                        && !isCardinalDirectionBlocked(WALL_BLOCK_SOUTH, here, there)
                        && !isCardinalDirectionBlocked(WALL_ALLOW_RANGE_SOUTH, here, there);
            } else if (x1 - 1 == x2 && y1 == y2) {
                walkable = !isCardinalDirectionBlocked(WALL_WEST, here, there)
                        && !isCardinalDirectionBlocked(WALL_BLOCK_WEST, here, there)
                        && !isCardinalDirectionBlocked(WALL_ALLOW_RANGE_WEST, here, there);
            } else if (x1 == x2 && y1 + 1 == y2) {
                walkable = !isCardinalDirectionBlocked(WALL_NORTH, here, there)
                                && !isCardinalDirectionBlocked(WALL_BLOCK_NORTH, here, there)
                                && !isCardinalDirectionBlocked(WALL_ALLOW_RANGE_NORTH, here, there);
            } else if (x1 + 1 == x2 && y1 == y2) {
                walkable = !isCardinalDirectionBlocked(WALL_EAST, here, there)
                        && !isCardinalDirectionBlocked(WALL_BLOCK_EAST, here, there)
                        && !isCardinalDirectionBlocked(WALL_ALLOW_RANGE_EAST, here, there);
            } else if (x1 - 1 == x2 && y1 - 1 == y2) {
                walkable = !isOrdinalDirectionBlocked(WALL_SOUTHWEST,
                        clipData[x1 + 1][y1 + 1],
                        clipData[x1 - 1][y1 + 1],
                        clipData[x1 + 1][y1 - 1],
                        clipData[x1 - 1][y1 - 1]
                );
            } else if (x1 - 1 == x2 && y1 + 1 == y2) {
                walkable = !isOrdinalDirectionBlocked(WALL_NORTHWEST,
                        clipData[x1 + 1][y1 + 1],
                        clipData[x1 - 1][y1 + 1],
                        clipData[x1 + 1][y1 - 1],
                        clipData[x1 - 1][y1 - 1]
                );
            } else if (x1 + 1 == x2 && y1 - 1 == y2) {
                walkable = !isOrdinalDirectionBlocked(WALL_SOUTHEAST,
                        clipData[x1 + 1][y1 + 1],
                        clipData[x1 - 1][y1 + 1],
                        clipData[x1 + 1][y1 - 1],
                        clipData[x1 - 1][y1 - 1]
                );
            } else if (x1 + 1 == x2 && y1 + 1 == y2) {
                walkable = !isOrdinalDirectionBlocked(WALL_NORTHEAST,
                        clipData[x1 + 1][y1 + 1],
                        clipData[x1 - 1][y1 + 1],
                        clipData[x1 + 1][y1 - 1],
                        clipData[x1 - 1][y1 - 1]
                );
            }
        }
        return walkable;
    }

    /**
     * Determines if the cardinal direction of your choice is blocked. The four Cardinal direction include:
     *  • North
     *  • East
     *  • South
     *  • West
     *
     * @param cardinal the cardinal direction to check. (Use corresponding flag from {@link TileFlags})
     * @param flag the flag
     * @param neighbor the neighbor
     * @return the boolean
     */
    public static boolean isCardinalDirectionBlocked(int cardinal, int flag, int neighbor) {
        boolean blocked = true;
        if (!isBlocked(neighbor)) {
            blocked = ((flag & cardinal) != 0);
        }
        return blocked;
    }


    /**
     * Determines if given tile flag represents a blocked/non-walkable tile.
     *
     * @param flag the flag of the given tile to check
     * @return true if the tile is blocked, and cannot be walked on; otherwise false.
     */
    public static boolean isBlocked(int flag) {
        return (flag & (OBJECT_MASK | DECORATION_BLOCK)) != 0;
    }

    /**
     * Is ordinal direction blocked.
     *
     * @param ordinal the ordinal
     * @param ne the ne
     * @param nw the nw
     * @param se the se
     * @param sw the sw
     * @return the boolean
     */
    public static boolean isOrdinalDirectionBlocked(int ordinal, int ne, int nw, int se, int sw) {
        boolean blocked = true;
        switch (ordinal) {
            case WALL_NORTHEAST:
                blocked = (((ne & (OBJECT_MASK)) != 0)
                        || ((sw & (OBJECT_MASK | WALL_WEST | WALL_SOUTH | WALL_SOUTHWEST)) != 0)
                        || ((se & (OBJECT_MASK | WALL_SOUTH)) != 0)
                        || ((nw & (OBJECT_MASK | WALL_WEST)) != 0));// || isACorner(ordinal, ne);
                break;
            case WALL_NORTHWEST:
                blocked = (((nw & (OBJECT_MASK)) != 0)
                        || ((se & (OBJECT_MASK | WALL_WEST | WALL_SOUTH | WALL_SOUTHWEST)) != 0)
                        || ((sw & (OBJECT_MASK | WALL_SOUTH)) != 0)
                        || ((ne & (OBJECT_MASK | WALL_WEST)) != 0));// || isACorner(ordinal, ne);
                break;
            case WALL_SOUTHEAST:
                blocked = (((se & (OBJECT_MASK)) != 0)
                        || ((nw & (OBJECT_MASK | WALL_WEST | WALL_SOUTH | WALL_SOUTHWEST)) != 0)
                        || ((ne & (OBJECT_MASK | WALL_SOUTH)) != 0)
                        || ((sw & (OBJECT_MASK | WALL_WEST)) != 0));// || isACorner(ordinal,ne);
                break;
            case WALL_SOUTHWEST:
                blocked = (((sw & (OBJECT_MASK)) != 0)
                        || ((ne & (OBJECT_MASK | WALL_WEST | WALL_SOUTH | WALL_SOUTHWEST)) != 0)
                        || ((nw & (OBJECT_MASK | WALL_SOUTH)) != 0)
                        || ((se & (OBJECT_MASK | WALL_WEST)) != 0));// || isACorner(ordinal,ne);
                break;
        }
        return blocked;
    }

    public int getDirection(int x, int y) {
        int direction = 0;
        if (x == 0 && y == -1) {
            direction = WALL_SOUTH;
        } else if (x == -1 && y == 0) {
            direction = WALL_WEST;
        } else if (x == 0 && y == 1) {
            direction = WALL_NORTH;
        } else if (x == 1 && y == 0) {
            direction = WALL_EAST;
        } else if (x == -1 && y == -1) {
            direction = WALL_SOUTHWEST;
        } else if (x == -1 && y == 1) {
            direction = WALL_NORTHWEST;
        } else if (x == 1 && y == -1) {
            direction = WALL_SOUTHEAST;
        } else if (x == 1 && y == 1) {
            direction = WALL_NORTHEAST;
        }
        return direction;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Collision Map: {\n");
        for(int[] key : clipData){
            builder.append(Arrays.toString(key)).append('\n');
        }
        builder.append("}");
        return builder.toString();
    }
}
