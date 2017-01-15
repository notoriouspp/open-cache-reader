package org.dreambot.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * ....
 *
 * @author Notorious
 * @version 0.0.1
 * @since 8/17/2016
 */
public class StdDraw {

    private static JFrame frame;
    private static JPanel panel;
    private static int width;
    private static int height;
    private static int offsetX;
    private static int offsetY;
    private static BufferedImage buffer;
    private static Graphics2D g;
    private static int scale;
    private static Color penColor;

    static {
        init();
    }

    private static GraphicsConfiguration configuration;


    private static void init(){
        frame = new JFrame("Standard Draw");
        frame.setSize(width = 512, height = 512);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        penColor = Color.RED;
        scale = 1;
        configuration = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        buffer = configuration.createCompatibleImage(width, height);
        g = buffer.createGraphics();
        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(buffer, 0, 0, frame);
            }
        };
        frame.setLayout(new GridLayout(1, 1));
        frame.add(panel);
    }

    public static void setSize(int width, int height){
        StdDraw.width = width;
        StdDraw.height = height;
        Dimension size = new Dimension(width, height);
        panel.setSize(size);
        panel.setPreferredSize(size);
        frame.pack();
    }

    public static Color getPenColor() {
        return penColor;
    }

    public static void setPenColor(Color penColor) {
        StdDraw.penColor = penColor;
    }

    public static void setScale(int scale) {
        StdDraw.scale = scale;
    }

    public static int getHeight() {
        return height;
    }

    public static int getWidth() {
        return width;
    }

    public static int getScale() {
        return scale;
    }

    public static void fill() {
        fill(penColor);
    }

    public static void fill(Color color) {
        g.setColor(color);
        g.fill(new Rectangle(0, 0, width, height));
        frame.setBackground(color);
        panel.repaint();
    }

    public static void drawString(String string, int x, int y) {
        g.setColor(penColor);
        g.drawString(string, offsetX + x, offsetY + y);
        panel.repaint();
    }

    public static void drawImage(Image image, int x, int y) {
        g.setColor(penColor);
        g.drawImage(image, offsetX + x, offsetY + y, frame);
        panel.repaint();
    }

    public static void fillRect(int x, int y, int w, int h) {
        g.setColor(penColor);
        g.fill(new Rectangle(x, y, w, h));
        panel.repaint();
    }

    public static void drawPoint(int x, int y, double radius) {
        g.setColor(penColor);
        g.fill(new Ellipse2D.Double(offsetX + x * scale, offsetY + y * scale, radius * 2, radius * 2));
        panel.repaint();
    }

    public static void setOffsetX(int offsetX) {
        StdDraw.offsetX = offsetX;
    }

    public static void setOffsetY(int offsetY) {
        StdDraw.offsetY = offsetY;
    }

    public static void setOffset(int offset) {
        StdDraw.offsetX = offset;
        StdDraw.offsetY = offset;
    }

    public static void drawPoint(Point point, double radius) {
        drawPoint(point.x, point.y, radius);
    }

    public static void drawPoint(Point point) {
        if(point != null) {
            drawPoint(point.x, point.y, 1D);
        }
    }

    public static void drawLine(Point start, Point end) {
        if (start != null && end != null) {
            g.setColor(penColor);
            g.drawLine(offsetX + start.x * scale,
                    offsetY + start.y * scale,
                    offsetX + end.x * scale,
                    offsetY + end.y * scale);
            panel.repaint();
        }
    }


    public static void show(){
        frame.pack();
        frame.setVisible(true);
    }
}
