package org.dreambot.cache.game.impl.runescape.oldschool.render;

/**
 * Created by Robert.
 * Time :   18:02.
 */
public class RSRasterizer {
    public static final int[] HSL_2_RGB = new int[0x10000];
    public static final int HUE_OFFSET = (int) (Math.random() * 17D) - 8;
    public static final int LIGHTNESS_OFFSET = (int) (Math.random() * 33D) - 16;

    static {
        calculatePalette(0.90000000000000002D);
    }

    public static int mixLightnessSigned(int hsl, int lightness) {

        if (hsl == -2)
            return 12345678;
        if (hsl == -1) {
            if (lightness < 0)
                lightness = 0;
            else if (lightness > 127)
                lightness = 127;
            lightness = 127 - lightness;
            return lightness;
        }
        lightness = (lightness * (hsl & 0x7f)) / 128;
        if (lightness < 2)
            lightness = 2;
        else if (lightness > 126)
            lightness = 126;
        return (hsl & 0xff80) + lightness;
    }

    /**
     * Packs separate hue, saturation and LIGHTNESS_BUFFER into a 18bit HSL word
     *
     * @param hue        The hue value to pack
     * @param saturation The saturation value to pack
     * @param lightness  The LIGHTNESS_BUFFER to pack
     * @return The packed HSL word
     */
    public static int packHSL(int hue, int saturation, int lightness) {
        if (lightness > 179)
            saturation /= 2;
        if (lightness > 192)
            saturation /= 2;
        if (lightness > 217)
            saturation /= 2;
        if (lightness > 243)
            saturation /= 2;
        return (hue / 4 << 10) + (saturation / 32 << 7) + lightness / 2;
    }

    private static int mix_lightness_gt(int hsl, int l) {
        if (hsl == -2)
            return 0xbc614e;
        if (hsl == -1) {
            if (l < 0)
                l = 0;
            else if (l > 127)
                l = 127;
            l = 127 - l;
            return l;
        }
        l = (l * (hsl & 0x7f)) / 128;
        if (l < 2)
            l = 2;
        else if (l > 126)
            l = 126;
        return (hsl & 0xff80) + l;
    }

    public static int mixLightness(int hsl, int l) {
        if (hsl == -1)
            return 0xbc614e;
        l = (l * (hsl & 0x7f)) / 128;
        if (l < 2)
            l = 2;
        else if (l > 126)
            l = 126;
        return (hsl & 0xff80) + l;
    }

    public static void calculatePalette(double brightness) {
        brightness += Math.random() * 0.029999999999999999D - 0.014999999999999999D;
        int hsl = 0;
        for (int k = 0; k < 512; k++) {
            double d1 = (double) (k / 8) / 64D + 0.0078125D;
            double d2 = (double) (k & 7) / 8D + 0.0625D;
            for (int k1 = 0; k1 < 128; k1++) {
                double d3 = (double) k1 / 128D;
                double r = d3;
                double g = d3;
                double b = d3;
                if (d2 != 0.0D) {
                    double d7;
                    if (d3 < 0.5D) {
                        d7 = d3 * (1.0D + d2);
                    } else {
                        d7 = (d3 + d2) - d3 * d2;
                    }
                    double d8 = 2D * d3 - d7;
                    double d9 = d1 + 0.33333333333333331D;
                    if (d9 > 1.0D) {
                        d9--;
                    }
                    double d11 = d1 - 0.33333333333333331D;
                    if (d11 < 0.0D) {
                        d11++;
                    }
                    if (6D * d9 < 1.0D) {
                        r = d8 + (d7 - d8) * 6D * d9;
                    } else if (2D * d9 < 1.0D) {
                        r = d7;
                    } else if (3D * d9 < 2D) {
                        r = d8 + (d7 - d8) * (0.66666666666666663D - d9) * 6D;
                    } else {
                        r = d8;
                    }
                    if (6D * d1 < 1.0D) {
                        g = d8 + (d7 - d8) * 6D * d1;
                    } else if (2D * d1 < 1.0D) {
                        g = d7;
                    } else if (3D * d1 < 2D) {
                        g = d8 + (d7 - d8) * (0.66666666666666663D - d1) * 6D;
                    } else {
                        g = d8;
                    }
                    if (6D * d11 < 1.0D) {
                        b = d8 + (d7 - d8) * 6D * d11;
                    } else if (2D * d11 < 1.0D) {
                        b = d7;
                    } else if (3D * d11 < 2D) {
                        b = d8 + (d7 - d8) * (0.66666666666666663D - d11) * 6D;
                    } else {
                        b = d8;
                    }
                }
                int byteR = (int) (r * 256D);
                int byteG = (int) (g * 256D);
                int byteB = (int) (b * 256D);
                int rgb = (byteR << 16) + (byteG << 8) + byteB;
                rgb = adjustBrightness(rgb, brightness);
                if (rgb == 0) {
                    rgb = 1;
                }
                HSL_2_RGB[hsl++] = rgb;
            }

        }
    }

    private static int adjustBrightness(int rgb, double intensity) {
        double r = (double) (rgb >> 16) / 256D;
        double g = (double) (rgb >> 8 & 0xff) / 256D;
        double b = (double) (rgb & 0xff) / 256D;
        r = Math.pow(r, intensity);
        g = Math.pow(g, intensity);
        b = Math.pow(b, intensity);
        int r_byte = (int) (r * 256D);
        int g_byte = (int) (g * 256D);
        int b_byte = (int) (b * 256D);
        return (r_byte << 16) + (g_byte << 8) + b_byte;
    }

    /**
     * Convert an 24 bit RGB colour word to an 18 bit HSL word
     * @param rgb The RGB colour word to convert
     * @return The converted HSL colour word
     */

    public static int RGBtoHSL(int rgb) {
        double r = (double) (rgb >> 16 & 0xff) / 256D;
        double g = (double) (rgb >> 8 & 0xff) / 256D;
        double b = (double) (rgb & 0xff) / 256D;
        double cmin = r;
        if (g < cmin)
            cmin = g;
        if (b < cmin)
            cmin = b;
        double cmax = r;
        if (g > cmax)
            cmax = g;
        if (b > cmax)
            cmax = b;
        double hue = 0.0D;
        double saturation = 0.0D;
        double lightness = (cmin + cmax) / 2D;
        if (cmin != cmax) {
            if (lightness < 0.5D)
                saturation = (cmax - cmin) / (cmax + cmin);
            if (lightness >= 0.5D)
                saturation = (cmax - cmin) / (2D - cmax - cmin);
            if (r == cmax)
                hue = (g - b) / (cmax - cmin);
            else if (g == cmax)
                hue = 2D + (b - r) / (cmax - cmin);
            else if (b == cmax)
                hue = 4D + (r - g) / (cmax - cmin);
        }
        hue /= 6D;
        int _hue = (int) (hue * 256D);
        int _saturation = (int) (saturation * 256D);
        int _lightness = (int) (lightness * 256D);
        if (_saturation < 0)
            _saturation = 0;
        else if (_saturation > 255)
            _saturation = 255;
        if (_lightness < 0)
            _lightness = 0;
        else if (_lightness > 255)
            _lightness = 255;
        //Noise was injected in the colour here for anti-cheat purposes.
        //This was removed because it wasn't needed
        return packHSL(_hue, _saturation, _lightness);
    }
}
