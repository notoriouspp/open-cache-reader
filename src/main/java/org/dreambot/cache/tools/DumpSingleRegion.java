package org.dreambot.cache.tools;

import org.dreambot.cache.game.impl.runescape.oldschool.region.Region;
import org.dreambot.cache.game.impl.runescape.oldschool.render.RSMapRenderer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static org.dreambot.util.Constants.PLANES;

/**
 * Created by Robert.
 * Time :   01:49.
 */
public class DumpSingleRegion {
    public static void main(String[] args) throws IOException {
        Region region = new Region(50, 50, 2,2);
        RSMapRenderer renderer = new RSMapRenderer(region);
        for (int i = 0; i < PLANES; i++) {
            File output = new File("regions\\region" + region.startX + "_" + region.startY + "_" + i + ".png");
            if(!output.exists()){
                output.mkdirs();
            }
            ImageIO.write(renderer.getPlaneImage(5, i), "png", output);
        }
    }
}
