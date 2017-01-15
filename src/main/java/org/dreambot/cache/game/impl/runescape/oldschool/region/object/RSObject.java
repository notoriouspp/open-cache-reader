package org.dreambot.cache.game.impl.runescape.oldschool.region.object;

import org.dreambot.cache.game.impl.runescape.oldschool.definition.ObjectDefinition;

/**
 * Created by Robert.
 * Time :   16:46.
 */
public class RSObject {




    public enum ObjectType {
        WALL_STRAIGHT,
        WALL_DIAGONAL_CONNECTOR,
        WALL_ENTIRE_CORNER,
        WALL_STRAIGHT_CORNER_CONNECTOR,
        WALL_DECORATION_STRAIGHT_INSIDE,
        WALL_DECORATION_STRAIGHT_OUTSIDE,
        WALL_DECORATION_DIAGONAL_OUTSIDE,
        WALL_DECORATION_DIAGONAL_INSIDE,
        IN_WALL_DECORATION_DIAGONAL,
        WALL_DIAGONAL,
        GENERAL_OBJECT,
        GROUND_OBJECT,
        ROOFS_SLOPED_STRAIGHT,
        ROOFS_SLOPED_DIAGONAL,
        ROOFS_SLOPE_DIAGONAL_CONNECTOR,
        ROOFS_SLOPED_STRAIGHT_CORNER_CONNECTOR,
        ROOFS_SLOPED_STRAIGHT_CORNER,
        ROOFS_STRAIGHT_TOP,
        ROOFS_STRAIGHT_EDGE,
        ROOFS_DIAGONAL_EDGE_CONNECTOR,
        ROOFS_STRAIGHT_EDGE_CONNECTOR,
        ROOFS_STRAIGHT_EDGE_CORNER_CONNECTOR,
        GROUND_DECORATION
    }


    public final ObjectType type;
    public final int objectId;
    public final int objectInfo;
    public final int objectOrientation;
    public final int mapScene;
    public final int height;
    public final int width;
    public final int mapZ;
    public final int x;
    public final int y;

    public RSObject(int x, int y, int objectId, int objectInfo, int objectType, int objectOrientation, int mapZ) {
        this.x = x;
        this.y = y;
        this.objectId = objectId;
        this.objectInfo = objectInfo;
        this.type = ObjectType.values()[objectType];
        this.objectOrientation = objectOrientation;
        this.mapScene = getDef().mapScene;
        this.width = getDef().sizeX;
        this.height = getDef().sizeY;
        this.mapZ = mapZ;
    }
    public boolean isWall() {
        return type == ObjectType.WALL_DIAGONAL ||
                type == ObjectType.WALL_STRAIGHT ||
                type == ObjectType.WALL_DIAGONAL_CONNECTOR ||
                type == ObjectType.WALL_ENTIRE_CORNER;
    }

    public ObjectDefinition getDef() {
        return ObjectDefinition.get(objectId);
    }

    @Override
    public String toString() {
        return "Object: {"
                + "\n\tname=" + getDef().name
                + "\n\tlocation=" + String.format("[X: %s, Y: %s, Z: %s]", x, y, mapZ)
                + "\n\tid=" + objectId
                + "\n\torientation=" + objectOrientation
                + "\n\ttype=" + type
                + "\n\tsolid=" + getDef().solid
                + "\n\tunwalkable-solid=" + getDef().impenetrable
                + "\n\thas-actions=" + getDef().actionsBoolean
                + "\n}";
    }
}
