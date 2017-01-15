package org.dreambot.cache.game.impl.runescape.oldschool.region;

import org.dreambot.cache.game.impl.runescape.oldschool.definition.ObjectDefinition;
import org.dreambot.cache.game.impl.runescape.oldschool.region.object.RSObject;
import org.dreambot.cache.game.impl.runescape.oldschool.region.tile.RSTile;
import org.dreambot.cache.tools.TileFlags;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 1/14/2017
 */
public class CollisionMap {

    public int setX = 0;
    public int clipHeight;
    public int clipWidth;
    public int setY = 0;
    public int[][] clipData;
    private RSRegionBlock block;

    public CollisionMap(RSRegionBlock block, int clipWidth, int clipHeight) {
        this.block = block;
        this.clipWidth = clipWidth;
        this.clipHeight = clipHeight;
        this.clipData = new int[this.clipWidth][this.clipHeight];
        this.reset();
    }

    public void reset() {
        for(int var2 = 0; var2 < this.clipWidth; ++var2) {
            for(int var3 = 0; var3 < this.clipHeight; ++var3) {
                if(var2 != 0 && 0 != var3 && var2 < this.clipWidth - 5 && var3 < this.clipHeight - 5) {
                    this.clipData[var2][var3] = TileFlags.UNLOADED_;
                } else {
                    this.clipData[var2][var3] = TileFlags.UNLOADED_;
                }
            }
        }
    }

    public void mark(RSObject object) {
        if(object == null){
            return;
        }
        ObjectDefinition objectDefinition = object.getDef();
        if (object.type.ordinal() == 10 || object.type.ordinal() == 11) {
            if (objectDefinition.solid && objectDefinition.name != null)
                markSolidOccupant(object.x, object.y, object.width, object.height, objectDefinition.solid);
            return;
        }
        if (object.type.ordinal() == 22) {
            if (objectDefinition.solid && objectDefinition.actionsBoolean)
                markBlocked(object.x, object.y);
        }
        if (object.type.ordinal() >= 12) {
            if (objectDefinition.solid && objectDefinition.actionsBoolean)
                markSolidOccupant(object.x, object.y, object.width, object.height, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 0) {
            if (objectDefinition.solid)
                markWall(object.x, object.y, object.type.ordinal(), object.objectOrientation, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 1) {
            if (objectDefinition.solid)
                markWall(object.x, object.y, object.type.ordinal(), object.objectOrientation, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 2) {
            if (objectDefinition.solid)
                markWall(object.x, object.y, object.type.ordinal(), object.objectOrientation, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 3) {
            if (objectDefinition.solid)
                markWall(object.x, object.y, object.type.ordinal(), object.objectOrientation, objectDefinition.impenetrable);
            return;
        }
        if (object.type.ordinal() == 9) {
            if (objectDefinition.solid)
                markSolidOccupant(object.x, object.y, object.width, object.height, objectDefinition.impenetrable);
            return;
        }
    }

    public void markWall(int x, int y, int type, int orientation, boolean impenetrable) {
        x -= this.setX;
        y -= this.setY;
        if(type == 0) {
            if(0 == orientation) {
                this.addClipLocation(x, y, TileFlags.WALL_WEST);
                this.addClipLocation(x - 1, y, TileFlags.WALL_EAST);
            }

            if(1 == orientation) {
                this.addClipLocation(x, y, TileFlags.WALL_NORTH);
                this.addClipLocation(x, y + 1, TileFlags.WALL_SOUTH);
            }

            if(2 == orientation) {
                this.addClipLocation(x, y, TileFlags.WALL_EAST);
                this.addClipLocation(1 + x, y, TileFlags.WALL_WEST);
            }

            if(3 == orientation) {
                this.addClipLocation(x, y, TileFlags.WALL_SOUTH);
                this.addClipLocation(x, y - 1, TileFlags.WALL_NORTH);
            }
        }

        if(type == 1 || 3 == type) {
            if(orientation == 0) {
                this.addClipLocation(x, y, TileFlags.WALL_NORTHWEST);
                this.addClipLocation(x - 1, 1 + y, TileFlags.WALL_SOUTHEAST);
            }

            if(orientation == 1) {
                this.addClipLocation(x, y, TileFlags.WALL_NORTHEAST);
                this.addClipLocation(1 + x, 1 + y, TileFlags.WALL_SOUTHWEST);
            }

            if(orientation == 2) {
                this.addClipLocation(x, y, TileFlags.WALL_SOUTHEAST);
                this.addClipLocation(1 + x, y - 1, TileFlags.WALL_NORTHWEST);
            }

            if(3 == orientation) {
                this.addClipLocation(x, y, TileFlags.WALL_SOUTHWEST);
                this.addClipLocation(x - 1, y - 1, TileFlags.WALL_NORTHEAST);
            }
        }

        if(type == 2) {
            if(0 == orientation) {
                this.addClipLocation(x, y, TileFlags.WALL_WEST | TileFlags.WALL_NORTH);
                this.addClipLocation(x - 1, y, TileFlags.WALL_EAST);
                this.addClipLocation(x, 1 + y, TileFlags.WALL_SOUTH);
            }

            if(1 == orientation) {
                this.addClipLocation(x, y, TileFlags.WALL_EAST | TileFlags.WALL_NORTH);
                this.addClipLocation(x, y + 1, TileFlags.WALL_SOUTH);
                this.addClipLocation(1 + x, y, TileFlags.WALL_WEST);
            }

            if(orientation == TileFlags.WALL_NORTH) {
                this.addClipLocation(x, y, TileFlags.WALL_EAST | TileFlags.WALL_SOUTH);
                this.addClipLocation(x + 1, y, TileFlags.WALL_WEST);
                this.addClipLocation(x, y - 1, TileFlags.WALL_NORTH);
            }

            if(orientation == 3) {
                this.addClipLocation(x, y, TileFlags.WALL_WEST | TileFlags.WALL_SOUTH);
                this.addClipLocation(x, y - 1, TileFlags.WALL_NORTH);
                this.addClipLocation(x - 1, y, TileFlags.WALL_EAST);
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

            if(type == TileFlags.WALL_NORTH) {
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

    public void markSolidOccupant(int x, int y, int width, int height, boolean solid) {
        int var7 = TileFlags.OBJECT_TILE;
        if(solid) {
            var7 += TileFlags.OBJECT_BLOCK;
        }
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
        this.clipData[x][y] |= TileFlags.OBJECT_BLOCK;
    }

    private void addClipLocation(int x, int y, int flag) {
        if(x < 0 || x >= clipWidth || y < 0 || y >= clipHeight){
            return;
        }
        this.clipData[x][y] |= flag;
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
