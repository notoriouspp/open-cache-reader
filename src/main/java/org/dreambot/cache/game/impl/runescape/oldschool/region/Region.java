package org.dreambot.cache.game.impl.runescape.oldschool.region;

import org.dreambot.algos.search.NotoSearch;
import org.dreambot.cache.game.impl.runescape.oldschool.definition.ObjectDefinition;
import org.dreambot.cache.fs.runescape.Container;
import org.dreambot.cache.fs.runescape.ReferenceTable;
import org.dreambot.cache.fs.runescape.data.CacheIndex;
import org.dreambot.cache.fs.runescape.data.ConfigArchive;
import org.dreambot.cache.game.impl.runescape.oldschool.region.object.RSObject;
import org.dreambot.cache.game.impl.runescape.oldschool.region.tile.RSTile;
import org.dreambot.cache.tools.CacheManager;
import org.dreambot.cache.tools.TileFlags;
import org.dreambot.util.StdDraw;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.dreambot.util.Constants.BLOCK_SIZE;
import static org.dreambot.util.Constants.MAX_BLOCKS;
import static org.dreambot.util.Constants.PLANES;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 1/14/2017
 */
public class Region {
    /**
     * The reference table.
     */
    public static ReferenceTable TABLE;

    static {
        try {
            TABLE = ReferenceTable.decode(Container.decode(CacheManager.get().getStore().read(CacheIndex.REFERENCE.getID(), ConfigArchive.INV.getID())).getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The region's start block x.
     */
    public final int startX;

    /**
     * The region's start block y.
     */
    public final int startY;

    /**
     * The region dimension in blocks.
     */
    public final Dimension blocksDimension = new Dimension();

    /**
     * The region dimension in tiles.
     */
    public final Dimension tilesDimension = new Dimension();


    /**
     * The blocks of this region.
     */
    public final RSRegionBlock[][] blocks;
    private int sizeX;
    private int sizeY;


    /**
     * The map region constructor.
     *
     * @param startX - the block start x.
     * @param startY - the block start y.
     */
    public Region(int startX, int startY) {
        this(startX, startY, 1, 1);
    }

    /**
     * The map region constructor.
     *
     * @param startX - the block start x.
     * @param startY - the block start y.
     * @param sizeX  - the width in blocks.
     * @param sizeY  - the height in blocks.
     */
    public Region(int startX, int startY, int sizeX, int sizeY) {
        this.startX = startX;
        this.startY = startY;
         blocks = new RSRegionBlock[MAX_BLOCKS][MAX_BLOCKS];

        int maxBlockX = Integer.MIN_VALUE;
        int maxBlockY = Integer.MIN_VALUE;

        int minBlockX = Integer.MAX_VALUE;
        int minBlockY = Integer.MAX_VALUE;

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        int totalSizeX = startX + this.sizeX;
        int totalSizeY = startY + this.sizeY;

        ArrayList<RSRegionBlockCallable> tasks = new ArrayList<>();
        for (int blockX = startX; blockX < totalSizeX; blockX++) {
            for (int blockY = startY; blockY < totalSizeY; blockY++) {
                int terrainID = CacheManager.getFileID(TABLE, "m" + blockX + "_" + blockY);
                int landscapeID = CacheManager.getFileID(TABLE, "l" + blockX + "_" + blockY);
                if (terrainID != -1 && landscapeID != -1) {
                    tasks.add(new RSRegionBlockCallable(blockX, blockY));
                    if (blockX > maxBlockX) {
                        maxBlockX = blockX;
                    }
                    if (blockY > maxBlockY) {
                        maxBlockY = (blockY);
                    }
                    if (blockX < minBlockX) {
                        minBlockX = (blockX);
                    }
                    if (blockY < minBlockY) {
                        minBlockY = (blockY);
                    }
                }
            }
        }
        loadBlocks(tasks);

        this.tilesDimension.setSize(sizeX, sizeY);
        this.tilesDimension.setSize((int) (BLOCK_SIZE * this.tilesDimension.getWidth()), (int) (BLOCK_SIZE * this.tilesDimension.getHeight()));

        System.out.println("Loaded region blocks and found:");
        System.out.println("----------------------------------------------------");
        System.out.println("minBlockX = " + minBlockX + " maxBlockX = " + maxBlockX);
        System.out.println("minBlockY = " + minBlockY + " maxBlockY = " + maxBlockY);
        System.out.println("----------------------------------------------------");
        System.out.println();
        System.out.println("----------------------------------------------------");
        System.out.println("blocksDimension = " + blocksDimension);
        System.out.println("tilesDimension = " + tilesDimension);
        System.out.println("----------------------------------------------------");
        System.out.println();
    }

    public BufferedImage getCollisionRender(int plane, int scale) {
        BufferedImage image = new BufferedImage(tilesDimension.width * scale, tilesDimension.height * scale, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setStroke(new BasicStroke(2));
        graphics.setComposite(AlphaComposite.SrcOver.derive(0.5f));
        for (int x = 0; x < tilesDimension.width; x++) {
            for (int y = 0; y < tilesDimension.height; y++) {
                graphics.setColor(isClipped(x, y) ? Color.RED : Color.GREEN);
                graphics.fill(new Rectangle(x * scale, (tilesDimension.height - y) * scale, 1, 1));
            }
        }
        return image;
    }

    public java.util.List<NotoSearch.SearchNode> generate(int plane, int step) {
        NotoSearch.SearchNode[][] nodes = getRawNodeMap(step);
        for (int x = 0; x < tilesDimension.width; x++) {
            for (int y = 0; y < tilesDimension.height; y++) {
                int mx = getBlockX(x);
                int my = getBlockY(y);
                RSRegionBlock block = blocks[mx][my];
                if(block != null) {
                    CollisionMap map = block.maps[plane];
                    mx = getBlockTileX(x, mx);
                    my = getBlockTileY(y, my);
                    if (mx > step) {
                        int x_ = mx - step;
                        translate(map, nodes, mx, my, x_, 0, TileFlags.WALL_WEST);
                    }
                    if (mx < map.clipWidth - step) {
                        int x_ = mx + step;
                        translate(map, nodes, mx, my, x_, 0, TileFlags.WALL_EAST);
                    }
                    if (my > step) {
                        int y_ = my - step;
                        translate(map, nodes, mx, my, 0, y_, TileFlags.WALL_NORTH);
                    }
                    if (my < map.clipHeight - step) {
                        int y_ = my + step;
                        translate(map, nodes, mx, my, 0, y_, TileFlags.WALL_SOUTH);
                    }
                }
            }
        }
        return Stream.of(nodes).flatMap(Stream::of).collect(Collectors.toList());
    }

    private static void translate(CollisionMap map, NotoSearch.SearchNode[][] nodes, int x, int y, int x_, int y_, int flag) {
        if((map.clipData[x_][y_] & flag) != 0){
            NotoSearch.SearchNode root = nodes[x][y];
            NotoSearch.SearchNode translated = nodes[x_][y_];
            if(!root.children.contains(translated) && !translated.children.contains(root)) {
                translated.children.add(root);
                root.children.add(translated);
            }
        }
    }

    private NotoSearch.SearchNode[][] getRawNodeMap(int step) {
        NotoSearch.SearchNode[][] nodes = new NotoSearch.SearchNode[(sizeX * BLOCK_SIZE) / step][(sizeY * BLOCK_SIZE) / step];
        for(int x = 0; x < tilesDimension.width; x += step){
            for(int y = 0; y < tilesDimension.height; y += step){
                nodes[x][y] = new NotoSearch.SearchNode(x, y);
            }
        }
        return nodes;
    }

    /**
     * Loads all blocks.
     *
     * @param tasks - the tasks to be executed.
     */
    private void loadBlocks(ArrayList<RSRegionBlockCallable> tasks) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (Future<RSRegionBlock> future : executor.invokeAll(tasks)) {
                RSRegionBlock block = future.get();
                blocks[block.blockX][block.blockY] = block;
            }
            executor.shutdown();
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the tile from this region.
     *
     * @param tileX - the global tile's 'x'.
     * @param tileY - the global tile's 'y'.
     * @param tileZ - the tile's 'z'.
     * @return the tile.
     */
    public RSTile getTile(int tileX, int tileY, int tileZ) {
        int blockX = getBlockX(tileX);
        int blockY = getBlockY(tileY);
        if (blockX < 0 || blockY < 0) {
            return null;
        }
        RSRegionBlock rsRegionBlock = blocks[blockX][blockY];
        if (rsRegionBlock == null) {
            return null;
        }
        return rsRegionBlock.getTile(tileZ, getBlockTileX(tileX, blockX), getBlockTileY(tileY, blockY));
    }

    /**
     * Translates global tile to block's one.
     *
     * @param globalTileX - the global tile.
     * @param blockX      - the block's x where we want to grab tile x.
     * @return the block's x.
     */

    public int getBlockTileX(int globalTileX, int blockX) {
        return globalTileX - (blockX - startX) * BLOCK_SIZE;
    }

    /**
     * Translates global tile to block's one.
     *
     * @param globalTileY - the global tile.
     * @param blockY      - the block's tile where we want to grab tile x.
     * @return the block's y.
     */
    public int getBlockTileY(int globalTileY, int blockY) {
        return globalTileY - (blockY - startY) * BLOCK_SIZE;
    }

    /**
     * Calculates block x from global tile x.
     *
     * @param tileX - the global tile's 'x'.
     * @return the block x for this tile.
     */
    public int getBlockX(int tileX) {
        return startX + tileX / BLOCK_SIZE;
    }

    /**
     * Calculates block y from global tile x.
     *
     * @param tileY - the global tile's 'y'.
     * @return the block y for this tile.
     */
    public int getBlockY(int tileY) {
        return startY + tileY / BLOCK_SIZE;
    }

    /**
     * Checks whether tile on 'x' ,'y' is clipped
     *
     * @param x - the tile's global 'x'
     * @param y - the tile's global 'y'
     * @return whether it is.
     */
    public boolean isClipped(int x, int y) {
        RSTile tile = getTile(x, y, 0);
        if (tile == null) {
            return true;
        }
        boolean wall = false;
        RSRegionBlock block = blocks[getBlockX(x)][getBlockY(y)];
        if(block != null){
            int flag = block.maps[0].clipData[getBlockTileX(x, block.blockX)][getBlockTileY(y, block.blockY)];
            wall = (flag & (TileFlags.WALL_MASK | TileFlags.OBJECT_MASK | TileFlags.WALL_BLOCK_MASK)) != 0;
        }
        int flag = tile.flag;
        for (RSObject object : tile.objects) {
            ObjectDefinition def = object.getDef();
            if (def.actions != null && Arrays.asList(def.actions).contains("Open")) {
                return false;
            }

            if (def.name != null && (def.name.contains("Gate") || def.name.contains("Door"))) {
                return false;
            }
            if (object.isWall() || def.impenetrable || !def.solid) {
                wall = true;
                break;
            }
        }
        for (int plane = 0; plane < PLANES; plane++) {
            tile = getTile(x, y, plane);
            if (tile != null) {
                boolean isBridge = (tile.flag & 0x2) == 0x2;
                if (isBridge) {
                    return false;
                }
            }
        }
        return (flag & 0x1) == 0x1 || wall;
    }


    /**
     * The callable that loads our requested block.
     */
    private static class RSRegionBlockCallable implements Callable<RSRegionBlock> {

        private final int blockX;
        private final int blockY;

        public RSRegionBlockCallable(int blockX, int blockY) {
            this.blockX = blockX;
            this.blockY = blockY;
        }

        public RSRegionBlock call() throws Exception {
            return new RSRegionBlock(blockX, blockY, true);
        }
    }
}
