package org.dreambot.cache.game.impl.runescape.oldschool.render;

import org.dreambot.cache.game.impl.runescape.oldschool.region.RSRegionBlock;
import org.dreambot.cache.game.impl.runescape.oldschool.region.RSScenes;
import org.dreambot.cache.game.impl.runescape.oldschool.region.Region;
import org.dreambot.cache.game.impl.runescape.oldschool.region.object.RSObject;
import org.dreambot.cache.game.impl.runescape.oldschool.region.tile.RSFloor;
import org.dreambot.cache.game.impl.runescape.oldschool.region.tile.RSTile;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.dreambot.util.Constants.BLOCK_SIZE;
import static org.dreambot.util.Constants.TILE_SIZE;

/**
 * Created by Robert.
 * Time :   15:06.
 */
public class RSMapRenderer {
    /**
     * Blend things
     */
    private final short[] lightnessBuffer;
    private final short[] hueBuffer;
    private final short[] saturationBuffer;
    private final short[] hueDivBuffer;
    private final short[] sizeBuffer;

    /**
     * The buffered image buffer.
     */
    private final BufferedImage imageBuffer;

    /**
     * The region we want to render.
     */
    private final Region region;

    public RSMapRenderer(Region region) {
        this.region = region;
        this.imageBuffer = new BufferedImage(region.tilesDimension.width * TILE_SIZE, region.tilesDimension.height * TILE_SIZE, BufferedImage.TYPE_INT_RGB);
        this.lightnessBuffer = new short[region.tilesDimension.height];
        this.hueBuffer = new short[region.tilesDimension.height];
        this.saturationBuffer = new short[region.tilesDimension.height];
        this.hueDivBuffer = new short[region.tilesDimension.height];
        this.sizeBuffer = new short[region.tilesDimension.height];
    }

    /**
     * Calculates color for each tile in this region.
     *
     * @param detailDeterminant - the - the blend sth, idk what.
     * @param tileZ
     * @return the color buffer.
     */
    public BufferedImage getPlaneImage(int detailDeterminant, int tileZ) {

        Graphics2D graphics = (Graphics2D) imageBuffer.getGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, imageBuffer.getWidth(), imageBuffer.getHeight());

        System.out.println("Rendering tiles ...");
        float tilesToRender = this.region.tilesDimension.width * this.region.tilesDimension.height + detailDeterminant * 4;
        float tilesRendered = 0;
        float lastPercent = 0;


        /**
         * Tile colors
         */
        for (int tileX = -detailDeterminant; tileX < this.region.tilesDimension.width + detailDeterminant; tileX++) {
            int h = 0;
            int s = 0;
            int l = 0;
            int shift = 0;
            int size = 0;
            blend(detailDeterminant, tileZ, tileX);
            if ((tilesRendered / (tilesToRender)) * 100f - lastPercent > 3) {
                lastPercent = (tilesRendered / (tilesToRender)) * 100f;
                System.out.printf("Rendering tiles: %.2f\n", lastPercent);
            }

            for (int tileY = -detailDeterminant; tileY < this.region.tilesDimension.height + detailDeterminant; tileY++) {
                int topY = tileY + detailDeterminant;
                int bottomY = tileY - detailDeterminant;
                boolean shouldAdd = topY >= 0 && topY < this.region.tilesDimension.height;
                boolean shouldSub = bottomY >= 0 && bottomY < this.region.tilesDimension.height;

                boolean drawX = tileX >= 0 && tileX < this.region.tilesDimension.width;
                boolean drawY = tileY >= 0 && tileY < this.region.tilesDimension.height;
                int blockX = this.region.getBlockX(tileX);
                int blockY = this.region.getBlockY(tileY);
                RSRegionBlock block = null;
                if (blockX >= 0 && blockY >= 0) {
                    block = this.region.blocks[blockX][blockY];
                }
                if (drawX) {

                    if (shouldAdd) {
                        h += hueBuffer[topY];
                        s += saturationBuffer[topY];
                        l += lightnessBuffer[topY];
                        shift += hueDivBuffer[topY];
                        size += sizeBuffer[topY];
                    }

                    if (shouldSub) {
                        h -= hueBuffer[bottomY];
                        s -= saturationBuffer[bottomY];
                        l -= lightnessBuffer[bottomY];
                        shift -= hueDivBuffer[bottomY];
                        size -= sizeBuffer[bottomY];
                    }

                    if (drawY && block != null) {
                        int blockTileX = this.region.getBlockTileX(tileX, blockX);
                        int blockTileY = this.region.getBlockTileY(tileY, blockY);
                        RSTile tile = block.getTile(tileZ, blockTileX, blockTileY);
                        if (tile != null) {
                            int underlay = tile.underlay & 0xff;
                            int overlay = tile.overlay & 0xff;
                            int color = getColor(h, s, l, shift, size, underlay, overlay);
                            int y = (this.region.tilesDimension.height - 1 - (tileY));
                            graphics.setColor(new Color(color));
                            graphics.fillRect(tileX * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
                tilesRendered++;
            }
        }
        System.out.printf("Rendering tiles: %.2f\n", 100f);

        System.out.println("Rendering objects ...");

        /**
         * Objects
         */
        for (int tileX = 0; tileX < this.region.tilesDimension.width; tileX++) {
            for (int tileY = 0; tileY < this.region.tilesDimension.height; tileY++) {
                int blockX = this.region.getBlockX(tileX);
                int blockY = this.region.getBlockY(tileY);

                RSRegionBlock block = null;
                if (blockX >= 0 && blockY >= 0) {
                    block = this.region.blocks[blockX][blockY];
                }
                if (block != null) {
                    int blockTileX = this.region.getBlockTileX(tileX, blockX);
                    int blockTileY = this.region.getBlockTileY(tileY, blockY);
                    RSTile tile = block.getTile(tileZ, blockTileX, blockTileY);
                    if (tile != null) {
                        for (RSObject object : tile.objects) {
                            if (-1 != object.mapScene) {
                                BufferedImage scene = RSScenes.SCENES_CACHE[object.mapScene];
                                if (scene != null) {
                                    int w = (object.width * TILE_SIZE - scene.getWidth()) / 2;
                                    int h = (object.height * TILE_SIZE - scene.getHeight()) / 2;
                                    graphics.drawImage(scene, (tileX * TILE_SIZE) + w, (this.region.tilesDimension.height - tileY - object.height) * TILE_SIZE + h, null);
                                }
                            } else {
                                drawObject(graphics, tileX, tileY, object);
                            }
                        }
                    }
                }


            }
        }
        return imageBuffer;
    }

    private void drawObject(Graphics2D graphics, int tileX, int tileY, RSObject object) {
        int heightInTiles = this.region.tilesDimension.height;
        RSObject.ObjectType type = object.type;
        int color = ((238 + (int) (Math.random() * 20D)) - 10 << 16) + ((238 + (int) (Math.random() * 20D)) - 10 << 8) + ((238 + (int) (Math.random() * 20D)) - 10);
        int secondaryColor = (238 + (int) (Math.random() * 20D)) - 10 << 16;
        boolean isDoor = false; /*type == RSObject.ObjectType.WALL_STRAIGHT &&
                object.getDef().actions != null &&
                Arrays.asList(object.getDef().actions).contains("Open")*/
        if (isDoor) {
            graphics.setColor(new Color(secondaryColor));
        } else {
            graphics.setColor(new Color(color));
        }
        int y1 = 0;
        int y2 = 0;
        int x1 = 0;
        int x2 = 0;
        int orientation = object.objectOrientation;
        if (type == RSObject.ObjectType.WALL_STRAIGHT || type == RSObject.ObjectType.WALL_ENTIRE_CORNER) {
            if (orientation == 0) {
                x1 = tileX * TILE_SIZE;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1 + TILE_SIZE - 1;
            } else if (orientation == 1) {
                x1 = tileX * TILE_SIZE;
                x2 = x1 + TILE_SIZE - 1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1;
            } else if (orientation == 2) {
                x1 = tileX * TILE_SIZE + TILE_SIZE - 1;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1 + TILE_SIZE - 1;
            } else if (orientation == 3) {
                x1 = tileX * TILE_SIZE;
                x2 = x1 + TILE_SIZE - 1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE + TILE_SIZE - 1;
                y2 = y1;
            }
            graphics.drawLine(x1, y1, x2, y2);
        } else if (type == RSObject.ObjectType.WALL_STRAIGHT_CORNER_CONNECTOR) {
            if (orientation == 0) {
                x1 = tileX * TILE_SIZE;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1;
            } else if (orientation == 1) {
                x1 = tileX * TILE_SIZE + TILE_SIZE - 1;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1;
            } else if (orientation == 2) {
                x1 = tileX * TILE_SIZE + TILE_SIZE - 1;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE + TILE_SIZE - 1;
                y2 = y1;
            } else if (orientation == 3) {
                x1 = tileX * TILE_SIZE;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE + TILE_SIZE - 1;
                y2 = y1;
            }
            graphics.drawLine(x1, y1, x2, y2);
        }
        if (type == RSObject.ObjectType.WALL_ENTIRE_CORNER) {
            if (orientation == 3) {
                x1 = tileX * TILE_SIZE;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE + TILE_SIZE - 1;
                y2 = y1;
                graphics.drawLine(x1, y1, x2, y2);
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1 + TILE_SIZE - 1;
                graphics.drawLine(x1, y1, x2, y2);
            } else if (orientation == 0) {

                x1 = tileX * TILE_SIZE;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1 + TILE_SIZE - 1;
                graphics.drawLine(x1, y1, x2, y2);
                x1 = tileX * TILE_SIZE;
                x2 = x1 + TILE_SIZE - 1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1;
                graphics.drawLine(x1, y1, x2, y2);
            } else if (orientation == 1) {
                x1 = tileX * TILE_SIZE;
                x2 = x1 + TILE_SIZE - 1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1;
                graphics.drawLine(x1, y1, x2, y2);
                x1 = tileX * TILE_SIZE + TILE_SIZE - 1;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1 + TILE_SIZE - 1;
                graphics.drawLine(x1, y1, x2, y2);
            } else if (orientation == 2) {
                x1 = tileX * TILE_SIZE + TILE_SIZE - 1;
                x2 = x1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1 + TILE_SIZE - 1;
                graphics.drawLine(x1, y1, x2, y2);
                x1 = tileX * TILE_SIZE;
                x2 = x1 + TILE_SIZE - 1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE + TILE_SIZE - 1;
                y2 = y1;
                graphics.drawLine(x1, y1, x2, y2);
            }
        } else if (type == RSObject.ObjectType.WALL_DIAGONAL) {
            if (orientation == 0 || orientation == 2) {
                x1 = tileX * TILE_SIZE + TILE_SIZE - 1;
                x2 = tileX * TILE_SIZE;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1 + TILE_SIZE - 1;
                graphics.drawLine(x1, y1, x2, y2);
            } else {
                x1 = tileX * TILE_SIZE;
                x2 = x1 + TILE_SIZE - 1;
                y1 = (heightInTiles - 1 - tileY) * TILE_SIZE;
                y2 = y1 + TILE_SIZE - 1;
                graphics.drawLine(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Blends floor colors and store data to buffers.
     *
     * @param detailDeterminant - the blend sth, idk what.
     * @param tileZ             - the plane.
     * @param tileStartX        - the tile start X, where blending starts.
     */
    private void blend(int detailDeterminant, int tileZ, int tileStartX) {
        for (int gradientY = 0; gradientY < this.region.tilesDimension.getHeight(); gradientY++) {
            int blockY = this.region.startY + gradientY / BLOCK_SIZE;
            int ty = this.region.getBlockTileY(gradientY, blockY);

            int tileRight = tileStartX + detailDeterminant;
            int blockRightX = this.region.startX + tileRight / BLOCK_SIZE;

            int tileLeft = tileStartX - detailDeterminant;
            int blockLeftX = this.region.startX + tileLeft / BLOCK_SIZE;
            RSRegionBlock blockRight = null, blockLeft = null;

            if (blockRightX >= this.region.startX) {
                blockRight = this.region.blocks[blockRightX][blockY];
            }

            if (blockLeftX >= this.region.startX) {
                blockLeft = this.region.blocks[blockLeftX][blockY];
            }

            if (tileRight >= 0 && tileRight < this.region.tilesDimension.getWidth()) {
                if (blockRight != null) {
                    int tx = this.region.getBlockTileX(tileRight, blockRightX);
                    RSTile rightTile = blockRight.getTile(tileZ, tx, ty);
                    if (rightTile != null) {
                        int tileType = rightTile.underlay & 0xff;
                        if (tileType > 0) {
                            RSFloor floor = RSFloor.UNDERLAY_CACHE[(tileType - 1)];

                            this.hueBuffer[gradientY] += floor.color.hue2;
                            this.saturationBuffer[gradientY] += floor.color.saturation;
                            this.lightnessBuffer[gradientY] += floor.color.lightness;
                            this.hueDivBuffer[gradientY] += floor.color.hueWeight;
                            this.sizeBuffer[gradientY]++;
                        }
                    }
                }
            }
            if (tileLeft >= 0 && tileLeft < this.region.tilesDimension.getWidth()) {
                if (blockLeft != null) {
                    int tx = this.region.getBlockTileX(tileLeft, blockLeftX);
                    RSTile leftTile = blockLeft.getTile(tileZ, tx, ty);
                    if (leftTile != null) {
                        int tileType = leftTile.underlay & 0xff;
                        if (tileType > 0) {
                            RSFloor floor = RSFloor.UNDERLAY_CACHE[(tileType - 1)];

                            this.hueBuffer[gradientY] -= floor.color.hue2;
                            this.saturationBuffer[gradientY] -= floor.color.saturation;
                            this.lightnessBuffer[gradientY] -= floor.color.lightness;
                            this.hueDivBuffer[gradientY] -= floor.color.hueWeight;
                            this.sizeBuffer[gradientY]--;
                        }
                    }
                }
            }
        }

    }

    /**
     * Calculates color from blending variables.
     *
     * @param gradientH    - the gradient's hue.
     * @param gradientS    - the gradient's saturation.
     * @param gradientL    - the gradient's lightness.
     * @param hueShift     - the gradient's hue divider.
     * @param bufferSize   - the gradient's size.
     * @param underlayType - the underlay type.
     * @param overlayType  - the overlay type.
     * @return the rgb color.
     */
    private int getColor(int gradientH, int gradientS, int gradientL, int hueShift, int bufferSize, int underlayType, int overlayType) {
        if (underlayType > 0 || overlayType > 0) {
            int underlayHSLReal = -1;
            int underlayHSL = -1;
            int underlayRGB = 0;

            if (underlayType > 0) {
                int h;
                int s;
                int l;

                h = (gradientH << 8) / hueShift;
                s = gradientS / bufferSize;
                l = gradientL / bufferSize;
                underlayHSLReal = RSRasterizer.packHSL(h, s, l);
                h = h + RSRasterizer.HUE_OFFSET & 0xff;
                l += RSRasterizer.LIGHTNESS_OFFSET;
                if (l < 0)
                    l = 0;
                else if (l > 255)
                    l = 255;

                if (h != -1)
                    underlayHSL = RSRasterizer.packHSL(h, s, l);
                if (underlayHSLReal == -1)
                    underlayHSLReal = underlayHSL;
            }

            if (underlayHSLReal != -1) {
                underlayRGB = RSRasterizer.HSL_2_RGB[RSRasterizer.mixLightness(underlayHSL, 96)];
            }
            if (overlayType == 0) {
                return underlayRGB;
            }

            RSFloor overlay = RSFloor.OVERLAY_CACHE[(overlayType - 1)];
            int overlayHSL;
            int overlayRGB = 0;
            if (overlay.texture >= 0) {
                overlayHSL = Textures.getColors(overlay.texture);
            } else if (overlay.color.rgbColor == 0xff00ff) {
                overlayHSL = -2;
            } else {
                int hue = overlay.color.hue + RSRasterizer.HUE_OFFSET & 0xFF;
                int lightness = RSRasterizer.LIGHTNESS_OFFSET + overlay.color.lightness;
                if (lightness < 0) {
                    lightness = 0;
                } else if (lightness > 255) {
                    lightness = 255;
                }
                overlayHSL = RSRasterizer.packHSL(hue, overlay.color.saturation, lightness);
            }
            if (overlayHSL != -2) {
                overlayRGB = RSRasterizer.HSL_2_RGB[RSRasterizer.mixLightnessSigned(overlayHSL, 96)];
            }

            if (overlay.secondColor != null) {

                int hue = overlay.secondColor.hue + RSRasterizer.HUE_OFFSET & 0xFF;
                int lightness = RSRasterizer.LIGHTNESS_OFFSET + overlay.secondColor.lightness;
                if (lightness < 0) {
                    lightness = 0;
                } else if (lightness > 255) {
                    lightness = 255;
                }

                overlayHSL = RSRasterizer.packHSL(hue, overlay.secondColor.saturation, lightness);
                overlayRGB = RSRasterizer.HSL_2_RGB[RSRasterizer.mixLightnessSigned(overlayHSL, 96)];
            }

            return overlayRGB;
        }

        return 0;
    }
}
