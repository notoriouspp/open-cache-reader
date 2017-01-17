package org.dreambot.cache.tools;

import org.dreambot.algos.search.RegionCrawler;
import org.dreambot.algos.search.astar.TileNode;
import org.dreambot.cache.game.impl.runescape.oldschool.region.Region;
import org.dreambot.cache.game.impl.runescape.oldschool.render.RSMapRenderer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.dreambot.util.Constants.PLANES;

/**
 * Created by Robert.
 * Time :   01:49.
 */
public class DumpSingleRegionCollision {
    public static void main(String[] args) throws IOException {
        Region region = new Region(50, 50, 4, 4);
        RSMapRenderer renderer = new RSMapRenderer(region);
        for (int i = 0; i < PLANES; i++) {
            File output = new File("regions\\region" + region.startX + "_" + region.startY + "_" + i + ".png");
            if(!output.exists()){
                output.mkdirs();
            }
            BufferedImage planeImage = renderer.getPlaneImage(5, i);
            if(i == 0){
                Graphics2D graphics = planeImage.createGraphics();
                List<TileNode> generate = RegionCrawler.generate(region,0, 3);
                graphics.setColor(Color.CYAN);
                generate.stream().filter(Objects::nonNull).filter(n -> !n.getNeighbors().isEmpty()).forEach(g -> {
                    int y = region.tilesDimension.height - g.y;
                    g.getNeighbors().forEach(n -> {
                        int y1 = region.tilesDimension.height - n.y;
                        graphics.drawLine(g.x * 4, y * 4, n.x * 4, y1 * 4);
                    });
                });
            }
            //planeImage.getGraphics().drawImage(region.getCollisionRender(0, 4), 0, 0, null);
            ImageIO.write(planeImage, "png", output);
        }
    }
}
