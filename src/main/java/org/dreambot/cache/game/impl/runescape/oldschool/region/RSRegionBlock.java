package org.dreambot.cache.game.impl.runescape.oldschool.region;

import org.dreambot.cache.fs.runescape.Container;
import org.dreambot.cache.fs.runescape.XTEALoader;
import org.dreambot.cache.fs.runescape.data.CacheIndex;
import org.dreambot.cache.game.impl.runescape.oldschool.region.object.RSObject;
import org.dreambot.cache.game.impl.runescape.oldschool.region.tile.RSTile;
import org.dreambot.cache.io.ByteBufferUtils;
import org.dreambot.cache.tools.CacheManager;
import org.dreambot.cache.tools.TileFlags;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.dreambot.util.Constants.BLOCK_SIZE;
import static org.dreambot.util.Constants.PLANES;

/**
 * Created by Robert.
 * Time :   00:12.
 */
public class RSRegionBlock {

    private final RSTile[][][] tiles = new RSTile[PLANES][BLOCK_SIZE][BLOCK_SIZE];

    public CollisionMap[] maps;
    public int blockX;
    public int blockY;

    public RSRegionBlock(int blockX, int blockY, boolean loadLandscape) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.maps = new CollisionMap[PLANES];
        for(int z = 0; z < PLANES; z++){
            this.maps[z] = new CollisionMap(this, BLOCK_SIZE, BLOCK_SIZE);
        }
        int terrainID = CacheManager.getFileID(Region.TABLE, "m" + blockX + "_" + blockY);
        int landscapeID = CacheManager.getFileID(Region.TABLE, "l" + blockX + "_" + blockY);
        if (terrainID != -1 && landscapeID != -1) {
            try {
                ByteBuffer data = Container.decode(CacheManager.get().getStore().read(CacheIndex.LANDSCAPES.getID(), terrainID)).getData();
                for (int z = 0; z < PLANES; z++) {
                    for (int tileX = 0; tileX < BLOCK_SIZE; tileX++) {
                        for (int tileY = 0; tileY < BLOCK_SIZE; tileY++) {
                            loadTerrainTile(tileX, tileY, z, data);
                        }
                    }
                }
                if (loadLandscape) {
                    int regionID = (blockX << 8 | blockY);
                    int[] keys = XTEALoader.getKeys(regionID);
                    if (keys[0] != 0 && keys[1] != 0 && keys[2] != 0 && keys[3] != 0) {
                        System.out.println(regionID);
                        Container landData = Container.decode(CacheManager.get().getStore().read(CacheIndex.LANDSCAPES.getID(), landscapeID), keys);
                        loadObjectTile(landData.getData());
                    }
                }
                createRegionScene();
            } catch (IOException e) {
                System.err.println("Couldn't read: " + blockX + "_" + blockY);
            }
        }
    }

    private void createRegionScene() {
        int plane;
        int x;
        int y;
        int originalPlane;
        for (plane = 0; plane < PLANES; ++plane) {
            for (x = 0; x < BLOCK_SIZE; ++x) {
                for (y = 0; y < BLOCK_SIZE; ++y) {
                    RSTile tile = getTile(plane, x, y);
                    if(tile != null) {
                        if ((tile.flag & 1) == 1) {
                            originalPlane = plane;
                            if ((tile.flag & 2) == 2) {
                                originalPlane = plane - 1;
                            }
                            if (originalPlane >= 0) {
                                maps[originalPlane].markBlocked(x, y);
                            }
                        }
                    }
                }
            }
        }
        for (x = 0; x < BLOCK_SIZE; ++x) {
            for (y = 0; y < BLOCK_SIZE; ++y) {
                RSTile tile = getTile(1, x, y);
                if(tile != null) {
                    if ((tile.flag & 2) == 2) {
                        maps[0].clipData[x][y] = TileFlags.UNLOADED_;
                    }
                }
            }
        }
    }


    public void loadObjectTile(ByteBuffer buffer) {
        {
            int objectId = -1;
            do {
                int objectIdOffset = ByteBufferUtils.getUnsignedSmart(buffer);
                if (objectIdOffset == 0)
                    return;
                objectId += objectIdOffset;
                int packedCoords = 0;
                do {
                    int packetPosOffset = ByteBufferUtils.getUnsignedSmart(buffer);
                    if (packetPosOffset == 0)
                        break;
                    packedCoords += packetPosOffset - 1;
                    int mapY = packedCoords & 63;
                    int mapX = packedCoords >> 6 & 63;
                    int mapZ = packedCoords >> 12;
                    int objSetting = buffer.get() & 0xFF;
                    int objType = objSetting >> 2;
                    int objFace = objSetting & 3;
                    RSTile tile = getTile(mapZ, mapX, mapY);
                    if (tile != null) {
                        if (mapZ == 1 && 2 == (tile.flag & 2)) {//bridge?
                            mapZ = mapZ - 1;
                        }
                        RSObject object = new RSObject(tile.x, tile.y, objectId, objSetting, objType, objFace, mapZ);
                        maps[mapZ].mark(object);
                        tile.objects.add(object);
                    }
                } while (true);
            } while (true);
        }
    }

    public void loadTerrainTile(int tileX, int tileY, int tileZ, ByteBuffer buffer) {
        if (tileX >= 0 && tileX < BLOCK_SIZE && tileY >= 0 && tileY < BLOCK_SIZE) {
            byte overlay = 0, underlay = 0, flag = 0;
            int opcode;
            while ((opcode = buffer.get() & 0xFF) != 0) {
                if (opcode == 1) {
                    buffer.get();
                    break;
                }
                if (opcode <= 49) {
                    overlay = (byte) (buffer.get() & 0xFF);
                } else if (opcode <= 81)
                    flag = (byte) (opcode - 49);
                else {
                    underlay = (byte) (opcode - 81);
                }
            }
            if (underlay != 0 || overlay != 0 || flag != 0) {
                RSTile rsTile = new RSTile(underlay, overlay, flag, tileX, tileY, tileZ);
                tiles[tileZ][tileX][tileY] = rsTile;
            }
        }
    }

    public RSTile getTile(int z, int x, int y) {
        return tiles[z][x][y];
    }
}
