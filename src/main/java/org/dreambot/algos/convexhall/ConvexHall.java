package org.dreambot.algos.convexhall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class ConvexHall {

    List<Point2D> points = new ArrayList<>();
    List<Point2D> removed = new ArrayList<>();


    public void addPoint(String label, double x, double y) {
        points.add(new Point2D(label, x, y));
    }


    public Collection<Point2D> calculate() {
        Collections.sort(points);

        Point2D startPoint = points.get(0);

        System.out.println(startPoint);

        System.out.println();


        Collections.sort(points, new PolarAngleComparator(startPoint));


        for (int i = 2; i < points.size(); i++) {

            int ccw = Point2D.ccw(points.get(i - 2), points.get(i - 1), points.get(i));

            if (ccw == 0) {
                removed.add(points.remove(i - 1));

                i--;
            } else if (ccw < 0) {
                int j = i;
                int removeIndex = i - 1;


                while ((Point2D.ccw(points.get(removeIndex - 1), points.get(removeIndex), points.get(j))) <= 0) {
                    removed.add(points.remove(removeIndex));
                    removeIndex--;
                    j--;
                    if (removeIndex == 0) {

                        break;
                    }
                }
                i -= j;
            }
        }
        return points;
    }


    public List<Point2D> getRemoved() {
        return removed;
    }
}