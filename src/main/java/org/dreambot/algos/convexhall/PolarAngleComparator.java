package org.dreambot.algos.convexhall;

import java.util.Comparator;

class PolarAngleComparator implements Comparator<Point2D> {


    private Point2D startPoint;

    public PolarAngleComparator(Point2D startPoint) {
        this.startPoint = startPoint;
    }

    @Override
    public int compare(Point2D o1, Point2D o2) {

        if (o1.equals(startPoint)) {
            return -1;
        }

        if (o2.equals(startPoint)) {
            return 1;
        }

        Double first = getAngle(o1);
        Double second = getAngle(o2);

        return first.compareTo(second);

    }

    private Double getAngle(Point2D point) {
        Double angle = (180 / Math.PI) * Math.atan((point.y - startPoint.y) / (point.x - startPoint.x));
        if (angle < 0) {
            angle = 180 + angle;
        }
        return angle;
    }
}