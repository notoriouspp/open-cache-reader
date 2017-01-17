package org.dreambot.cache.game.impl.runescape.oldschool.definition;

import org.dreambot.cache.fs.runescape.Archive;
import org.dreambot.cache.fs.runescape.Cache;
import org.dreambot.cache.fs.runescape.Container;
import org.dreambot.cache.fs.runescape.ReferenceTable;
import org.dreambot.cache.fs.runescape.data.CacheIndex;
import org.dreambot.cache.fs.runescape.data.ConfigArchive;
import org.dreambot.cache.io.ByteBufferUtils;
import org.dreambot.util.Preconditions;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ObjectDefinition {
    public static int MAX_OBJECTS = 32000;
    private static final ObjectDefinition[] CACHE = new ObjectDefinition[MAX_OBJECTS];

    private static final int CONFIG_DEFINITIONS_CACHE_INDEX = 2;
    private static final int OBJECT_DEFINITION_INDEX_FILE = 6;
    private static Archive archive;

    private static ObjectDefinition[] objs;

    public static void initialize(Cache cache) {
        int count = 0;
        try {
            Container container = Container.decode(cache.getStore().read(CacheIndex.REFERENCE, CacheIndex.CONFIGS));
            ReferenceTable table = ReferenceTable.decode(container.getData());

            ReferenceTable.Entry entry = table.getEntry(ConfigArchive.OBJECT);
            Archive archive = Archive.decode(cache.read(CacheIndex.CONFIGS, ConfigArchive.OBJECT).getData(),
                    entry.size());

            objs = new ObjectDefinition[entry.capacity()];
            for (int id = 0; id < entry.capacity(); id++) {
                ReferenceTable.ChildEntry child = entry.getEntry(id);
                if (child == null)
                    continue;

                ByteBuffer buffer = archive.getEntry(child.index());
                ObjectDefinition type =  ObjectDefinition.decode(buffer, id);
                objs[id] = type;
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error Loading ObjectType(s)!");
            e.printStackTrace();
        }
        System.out.println("Loaded " + count + " ObjectDefinitions(s)!");
    }

    public static ObjectDefinition get(int id) {
        Preconditions.checkArgument(id >= 0, "ID can't be negative!");
        Preconditions.checkArgument(id < objs.length, "ID can't be greater than the max object id!");
        return objs[id];
    }


    public String name;
    public int id;

    public boolean unknown;
    private byte lightAmbient;
    private int translateX;
    private int modelSizeZ;
    private byte lightDiffuse;
    public int sizeX;
    private int translateY;
    private boolean interactable;
    public int icon;
    private int[] originalModelColors;
    private int modelSizeX;
    public int configId;
    public boolean rotated;
    public int mapScene;
    public int[] childrenIds;
    public int solidInt;
    public int sizeY;
    public boolean adjustToTerrain;
    public boolean aBoolean269;
    public boolean solid;
    public boolean impenetrable;
    public int face;
    private boolean delayShading;
    private static int cacheIndex;
    private int modelSizeY;
    private int[] modelIds;
    public int varbitId;
    public int unknown4;
    private int[] modelTypes;
    public byte[] description;
    public boolean actionsBoolean;
    public boolean castsShadow;
    public int animationId;
    private int translateZ;
    private int[] modifiedModelColors;
    public String[] actions;

    public ObjectDefinition(int id) {
        this.id = id;
        setDefaultValues();
    }


    private void setDefaultValues() {
        modelIds = null;
        modelTypes = null;
        name = null;
        description = null;
        modifiedModelColors = null;
        originalModelColors = null;
        sizeX = 1;
        sizeY = 1;
        solid = true;
        impenetrable = true;
        actionsBoolean = false;
        adjustToTerrain = false;
        delayShading = false;
        aBoolean269 = false;
        animationId = -1;
        unknown4 = 16;
        lightAmbient = (byte) 0;
        lightDiffuse = (byte) 0;
        actions = null;
        icon = -1;
        mapScene = -1;
        rotated = false;
        castsShadow = true;
        modelSizeX = 128;
        modelSizeY = 128;
        modelSizeZ = 128;
        face = 0;
        translateX = 0;
        translateY = 0;
        translateZ = 0;
        unknown = false;
        solidInt = -1;
        varbitId = -1;
        configId = -1;
        childrenIds = null;
    }

    /**
     * @param buffer A {@link java.nio.ByteBuffer} that contains information such as the
     *               items location.
     * @return a new ItemDefinition.
     */
    public static ObjectDefinition decode(ByteBuffer buffer, int id) {

        ObjectDefinition def = new ObjectDefinition(id);
        def.id = id;
//        def.groundOptions = new String[]{null, null, "take", null, null};
//        def.inventoryOptions = new String[]{null, null, null, null, "drop"};
//        while (true) {
//            int opcode = buffer.getResource() & 0xFF;
//            if (opcode == 0) {
//                break;
//            }
//            if (opcode == 1) {
//				
//            } else {
//                System.out.println("Unknown opcode: " + opcode);
//                break;
//            }
//        }

        int hasActionsInt = -1;
        while (true) {
            int opcode = buffer.get() & 0xFF;
            if (opcode == 0) {
                break;
            }
            if (opcode == 1) {
                int modelCount = buffer.get() & 0xFF;
                if (modelCount > 0) {
                    if (def.modelIds == null) {
                        def.modelTypes = new int[modelCount];
                        def.modelIds = new int[modelCount];
                        for (int model = 0; model < modelCount; model++) {
                            def.modelIds[model] = buffer.getShort() & 0xFFFFF;
                            def.modelTypes[model] = buffer.get() & 0xFF;
                        }
                    } else {
                        buffer.position(buffer.position() + (modelCount * 3));
                    }
                }
            } else if (opcode == 2) {
                def.name = ByteBufferUtils.getJagexString(buffer);
//			} else if (opcode == 3) {
//				def.description = ByteBufferUtils.getJagexString(buffer).getBytes();
            } else if (opcode == 5) {
                int modelCount = buffer.get() & 0xFF;
                if (modelCount > 0) {
                    if (def.modelIds == null) {
                        def.modelTypes = null;
                        def.modelIds = new int[modelCount];
                        for (int model = 0; model < modelCount; model++) {
                            def.modelIds[model] = buffer.getShort() & 0xFFFFF;
                        }
                    } else {
                        buffer.position(buffer.position() + (modelCount * 2));
                    }
                }
            } else if (opcode == 14) {
                def.sizeX = buffer.get() & 0xFF;
            } else if (opcode == 15) {
                def.sizeY = buffer.get() & 0xFF;
            } else if (opcode == 17) {
                def.solid = false;
            } else if (opcode == 18) {
                def.impenetrable = false;
            } else if (opcode == 19) {
                hasActionsInt = buffer.get() & 0xFF;
                if (hasActionsInt == 1) {
                    def.actionsBoolean = true;
                }
            } else if (opcode == 21) {
                def.adjustToTerrain = true;
            } else if (opcode == 22) {
                def.delayShading = true;
            } else if (opcode == 23) {
                def.aBoolean269 = true;
            } else if (opcode == 24) {
                def.animationId = buffer.getShort() & 0xFFFFF;
                if (def.animationId == 65535) {
                    def.animationId = -1;
                }
            } else if (opcode == 27) {
                //ignopre
            } else if (opcode == 28) {
                def.unknown4 = buffer.get() & 0xFF;
            } else if (opcode == 29) {
                def.lightAmbient = buffer.get();
            } else if (opcode == 39) {
                def.lightDiffuse = buffer.get();
            } else if (opcode >= 30 && opcode < 35) {
                if (def.actions == null) {
                    def.actions = new String[5];
                }
                def.actions[opcode - 30] = ByteBufferUtils.getJagexString(buffer);
                if (def.actions[opcode - 30].equalsIgnoreCase("hidden")) {
                    def.actions[opcode - 30] = null;
                }
            } else if (opcode == 40) {
                int modelColorCount = buffer.get() & 0xFF;
                def.modifiedModelColors = new int[modelColorCount];
                def.originalModelColors = new int[modelColorCount];
                for (int modelColor = 0; modelColor < modelColorCount; modelColor++) {
                    def.modifiedModelColors[modelColor] = buffer.getShort() & 0xFFFFF;
                    def.originalModelColors[modelColor] = buffer.getShort() & 0xFFFFF;
                }
            } else if (opcode == 41) {
                int modelColorCount = buffer.get() & 0xFF;
//				def.modifiedModelColors = new int[modelColorCount];
//				def.originalModelColors = new int[modelColorCount];
                for (int modelColor = 0; modelColor < modelColorCount; modelColor++) {
                    /*def.modifiedModelColors[modelColor] = */
                    buffer.getShort();
                    /*def.originalModelColors[modelColor] = */
                    buffer.getShort();
                }
            } else if (opcode == 60) {
                def.icon = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 62) {
                def.rotated = true;
            } else if (opcode == 64) {
                def.castsShadow = false;
            } else if (opcode == 65) {
                def.modelSizeX = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 66) {
                def.modelSizeY = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 67) {
                def.modelSizeZ = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 68) {
                def.mapScene = buffer.getShort() & 0xFFFFF;
            } else if (opcode == 69) {
                def.face = buffer.get() & 0xFF;
            } else if (opcode == 70) {
                def.translateX = buffer.getShort();
            } else if (opcode == 71) {
                def.translateY = buffer.getShort();
            } else if (opcode == 72) {
                def.translateZ = buffer.getShort();
            } else if (opcode == 73) {
                def.unknown = true;
            } else if (opcode == 74) {
                def.solid = false;
                def.impenetrable = false;
            } else if (opcode == 75) {
                def.solidInt = buffer.get() & 0xFF;
            } else if (opcode == 77) {
                def.varbitId = buffer.getShort() & 0xFFFFF;
                if (def.varbitId == 65535) {
                    def.varbitId = -1;
                }
                def.configId = buffer.getShort() & 0xFFFFF;
                if (def.configId == 65535) {
                    def.configId = -1;
                }
                int childrenCount = buffer.get() & 0xFF;
                def.childrenIds = new int[childrenCount + 1];
                for (int child = 0; child <= childrenCount; child++) {
                    def.childrenIds[child] = buffer.getShort() & 0xFFFFF;
                    if (def.childrenIds[child] == 65535) {
                        def.childrenIds[child] = -1;
                    }
                }
            } else if (78 == opcode) {
                buffer.getShort();
                buffer.get();
//         this.anInt2172 = buffer.getUnsignedLEShort((byte)0) * -2037562441;
//         this.anInt2173 = buffer.readUnsignedByte(1426091708) * -74006173;
            } else if (79 == opcode) {
                buffer.getShort();
                buffer.getShort();
                buffer.get();
                int length = buffer.get();
                for (int var5 = 0; var5 < length; ++var5) {
                    buffer.getShort();
                }
//         this.anInt2177 = buffer.getUnsignedLEShort((byte)0) * 274297595;
//         this.anInt2175 = buffer.getUnsignedLEShort((byte)0) * 1716926479;
//         this.anInt2173 = buffer.readUnsignedByte(1181016775) * -74006173;
//         var4 = buffer.readUnsignedByte(-192393308);
//         this.anIntArray2165 = new int[var4];
//
//         for(var5 = 0; var5 < var4; ++var5) {
//            this.anIntArray2165[var5] = buffer.getUnsignedLEShort((byte)0);
//         }
            } else if (81 == opcode) {
                buffer.get();
//         this.anInt2147 = buffer.readUnsignedByte(540593542) * -268392192;
            } else {
                System.out.println("Unkown opcode: " + opcode);
            }
        }
        if (hasActionsInt == -1) {
            def.interactable = def.modelIds != null && (def.modelTypes == null || def.modelTypes[0] == 10);
            if (def.actions != null)
                def.interactable = true;
        }
        return def;
    }
}



/*
public class ObjectDefinition {

    private int id;
    private String name;
    private String[] options;
    private int[] modelIds;
    private int[] toObjectIds;
    private short[] originalColors;
    private short[] modifiedColors;
    private int width;
    private int height;
    private int objectAnimation;
    private int animation;
    private int groundDecorationSprite;
    private int scaleX;
    private int scaleY;
    private int scaleZ;
    private int mapSceneSprite;
    private int shadding;
    private int lightness;


    private int clipType;
    private boolean notClipped;
    private boolean projectileClipped;
    private boolean isSolid;
    private boolean isWalkable;
    private boolean hasOptions;


    public ObjectDefinition() {
        width = 1;
        height = 1;
        options = new String[5];
        objectAnimation = -1;
        notClipped = false;
        projectileClipped = true;
        clipType = 2;
    }


    public static ObjectDefinition decode(ByteBuffer buf, int id) {
        ObjectDefinition def = new ObjectDefinition();
        def.id = id;
        outer:
        while (true) {
            int opcode = buf.getResource() & 0xFF;
            switch (opcode) {
                case 1:
                    // Skip model information
                    int size = buf.getResource() & 0xFF;
                    if (size > 0) {
                        for (int idx = 0; idx < size; idx++) {
                            buf.getShort();
                            buf.getResource();
                        }
                    }
                    break;

                case 2:
                    def.name = ByteBufferUtils.getJagexString(buf);
                    break;

                case 5:

                    // Skip model information
                    size = buf.getResource() & 0xFF;
                    def.modelIds = new int[size];
                    if (size > 0) {
                        for (int idx = 0; idx < size; idx++) {
                            def.modelIds[idx] = buf.getShort();
                        }
                    }
                    break;

                case 14:
                    def.width = buf.getResource() & 0xFF;
                    break;

                case 15:
                    def.height = buf.getResource() & 0xFF;
                    break;

                case 17:

                    def.clipType = 0;
                    def.projectileClipped = false;
                    def.isSolid = false;
                    def.isWalkable = false;

                    break;

                case 18:
                    def.isWalkable = false;
                    break;

                case 19:
                    def.hasOptions = buf.getResource() == 1;
                    break;

                case 24:
                    def.animation = buf.getShort() & 0xFFFFF;
                    if (def.animation == 65535) {
                        def.animation = -1;
                    }
                    break;

                case 27:
                    def.clipType = 1;
                    def.isSolid = true;
                    break;

                case 28:
                    // Unknown field(s)
                    buf.getResource();
                    break;

                case 29:
                    def.lightness = buf.getResource();
                    break;

                case 30:
                case 31:
                case 32:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                    // opcode - 30
                    String option = ByteBufferUtils.getJagexString(buf);
                    if (!def.hasOptions && def.options == null) {
                        def.options = new String[5];
                    }
                    def.options[opcode - 30] = option;
                    break;

                case 39:
                   def.shadding = buf.getResource() * 5;
                    break;

                case 40:
                    size = buf.getResource() & 0xFF;
                    def.modifiedColors = new short[size];
                    def.originalColors = new short[size];
                    for (int idx = 0; size > idx; idx++) {
                        def.originalColors[idx] = (short) (buf.getShort() & 0xFFFFF);
                        def.modifiedColors[idx] = (short) (buf.getShort() & 0xFFFFF);
                    }
                    break;

                case 60:

                    def.groundDecorationSprite = buf.getShort() & 0xFFFFF;
                    break;

                case 65:

                    def.scaleX = buf.getShort() & 0xFFFFF;
                    break;

                case 66:

                    def.scaleY = buf.getShort() & 0xFFFFF;
                    break;

                case 67:

                    def.scaleZ = buf.getShort() & 0xFFFFF;
                    break;

                case 68:

                    def.mapSceneSprite = buf.getShort() & 0xFFFFF;
                    break;

                case 69:
                    buf.getResource();
                    break;

                case 70:
                case 71:
                case 72:
                    buf.getShort();
                    break;

                case 74:
                    // not clipped = true
                    break;

                case 77:
                    int a = buf.getShort() & 0xFFFFF;
                    if (a == 65535) {
                        a = -1;
                    }
                    int b = buf.getShort() & 0xFFFFF;
                    if (b == 65535) {
                        b = -1;
                    }
                    size = buf.getResource() & 0xFF;
                    for (int idx = 0; idx <= size; idx++) {
                        buf.getShort();
                    }
                    break;

                case 78:
                    buf.getShort();
                    buf.getResource();
                    break;

                case 79:
                    buf.getShort();
                    buf.getShort();
                    buf.getResource();
                    size = buf.getResource() & 0xFF;
                    for (int idx = 0; idx < size; idx++) {
                        buf.getShort();
                    }
                    break;

                case 0:
                    break outer;

                default:
                    if (opcode != 75) {
                        break outer;
                    }
                    buf.getResource();
                    break;
            }

        }
        return def;
    }

    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("ObjectDefinition(");
        builder.append("ID=").append(id).append(",");
        builder.append("Name=").append(name).append(",");
        builder.append("Options=").append(Arrays.toString(options)).append(",");
        builder.append("Model-IDs=").append(Arrays.toString(modelIds)).append(",");
        builder.append("SizeX=").append(width).append(",");
        builder.append("SizeY=").append(height).append(",");
        builder.append("Clipped=").append(!notClipped).append(",");
        builder.append("ProjectileClipped=").append(projectileClipped).append(",");
        builder.append("ClipType=").append(clipType).append(")");

        return builder.toString();
    }
}
*/