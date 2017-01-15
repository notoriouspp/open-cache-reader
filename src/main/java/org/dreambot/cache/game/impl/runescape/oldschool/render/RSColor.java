package org.dreambot.cache.game.impl.runescape.oldschool.render;

/**
 * Created by Robert.
 * Time :   13:50.
 */
public class RSColor {
    public int hue;
    public int saturation;
    public int lightness;
    public int hue2;
    public int hueWeight;
    public int rgbColor;

    public RSColor(int rgbColor) {
        this.rgbColor = rgbColor;
        this.calcHSL();
    }

    public RSColor(int h, int s, int l) {
        this.rgbColor = RSRasterizer.HSL_2_RGB[RSRasterizer.packHSL(h, s, l)];
        this.calcHSL();
    }


    private void calcHSL() {
        double red = (double) (rgbColor >> 16 & 0xff) / 256D;
        double green = (double) (rgbColor >> 8 & 0xff) / 256D;
        double blue = (double) (rgbColor & 0xff) / 256D;

        double cmin = Math.min(red, Math.min(green, blue));
        double cmax = Math.max(red, Math.max(green, blue));

        double hue = 0.0D;
        double saturation = 0.0D;
        double lightness = (cmax + cmin) / 2D;
        if (cmin != cmax) {
            if (lightness < 0.5D)
                saturation = (cmax - cmin) / (cmax + cmin);
            if (lightness >= 0.5D)
                saturation = (cmax - cmin) / (2D - cmax - cmin);
            if (red == cmax)
                hue = (green - blue) / (cmax - cmin);
            else if (green == cmax)
                hue = 2D + (blue - red) / (cmax - cmin);
            else if (blue == cmax)
                hue = 4D + (red - green) / (cmax - cmin);
        }
        hue /= 6D;
        this.hue = (int) (hue * 256D);
        this.saturation = (int) (saturation * 256D);
        this.lightness = (int) (lightness * 256D);
        if (this.saturation < 0) {
            this.saturation = 0;
        } else if (this.saturation > 255) {
            this.saturation = 255;
        }
        if (this.lightness < 0) {
            this.lightness = 0;
        } else if (this.lightness > 255) {
            this.lightness = 255;
        }
        if (lightness > 0.5D) {
            this.hueWeight = (int) ((1.0D - lightness) * saturation * 512D);
        } else {
            this.hueWeight = (int) (lightness * saturation * 512D);
        }
        if (this.hueWeight < 1) {
            this.hueWeight = 1;
        }
        this.hue2 = (int) (hue * (double) hueWeight);
    }
}
