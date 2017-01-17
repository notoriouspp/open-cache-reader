package org.dreambot.algos.convexhall;


public class Point2D implements Comparable<Point2D> {


    public final String label;
    public final Double x, y;

    public Point2D(String label, double x, double y) {
        this.label = label;
        this.x = x;
        this.y = y;
    }


    public static int ccw(Point2D a, Point2D b, Point2D c) {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);


        if (area2 < 0) {
            return -1;
        } else if (area2 > 0) {
            return 1;
        } else {     //  counter-clockwise
            return 0;
        }
    }

    @Override
    public int compareTo(Point2D o) {
        return this.y.compareTo(o.y);
    }

    @Override
    public String toString() {
        return "Point2D{" +
                "label='" + label + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}