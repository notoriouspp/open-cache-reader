package org.dreambot.algos.search.peucker;

import java.awt.geom.Point2D;

import static java.lang.Math.*;

/**
 * A utility class that provides the basic vector operations
 */
public final class VectorUtils {

    /**
     * finds the distance from the point to the segment
     *
     * @param p the point
     * @param start the start point of the segment
     * @param end the end point of the segment
     * @return the distance to the segment
     */
    public static double distanseToSegment(Point2D p, Point2D start, Point2D end){
        Point2D r  = vectDif(end, start),
                r1 = vectDif(p, start),
                r2 = vectDif(p, end);

        double r_2  = vectSquare(r),
               r1_2 = vectSquare(r1),
               r2_2 = vectSquare(r2);

        return (r2_2 >= r_2 + r1_2)? Math.sqrt(r1_2) :
               (r1_2 >= r_2 + r2_2)? Math.sqrt(r2_2) :
                       Math.abs(vectProduct(r,r1)/sqrt(r_2));
    }

    /**
     * checks whether two segments are intersected
     *
     * @param a the start of the first segment
     * @param b the end of the first segment
     * @param c the start of the second segment
     * @param d the end of the second segment
     * @return
     */
    public static boolean intersect(Point2D a, Point2D b, Point2D c, Point2D d){
        Point2D ab = vectDif(a,b), ac = vectDif(a,c), ad = vectDif(a,d);
        Point2D cd = vectDif(c, d), ca = vectDif(c, a), cb = vectDif(c, b);
        return intersect(a.getX(),b.getX(),c.getX(),d.getX()) &&
                vectProduct(ab,ac) *  vectProduct(ab,ad) <=0  &&
                vectProduct(cd,ca) *  vectProduct(cd,cb) <=0;
    }

    /**
     * checks whether two segments that lie on a line  are intersected
     *
     * @param a the start of the first segment
     * @param b the end of the first segment
     * @param c the start of the second segment
     * @param d the end of the second segment
     * @return
     */
    public static boolean intersect(double a, double b, double c, double d) {
        double p;
        if(a > b) { p = a; a = b; b = p; }
        if(c > d) { p = c; c = d; d = p;  }
        return max(a,c) <= min(b,d);
    }

    /**
     * finds intersected point of two segments
     *
     * @param a the start of the first segment
     * @param b the end of the first segment
     * @param c the start of the second segment
     * @param d the end of the second segment
     * @return the intersected point
     *         null if the segments is not intersected
     *
     */
    public static Point2D intersectPoint(Point2D a, Point2D b, Point2D c, Point2D d) {
        if(!intersect(a,b,c,d)) return  null;

        if(Double.compare(a.getX(),b.getX()) == 0 && Double.compare(a.getY(),b.getY()) == 0 )
            return a;
        if(Double.compare(c.getX(),d.getX()) == 0 && Double.compare(c.getY(),d.getY()) == 0 )
            return c;

        double A1 = b.getY() - a.getY(), A2 = d.getY() - c.getY(),
               B1 = a.getX() - b.getX(), B2 = c.getX() - d.getX(),
               C1 = A1 * a.getX() + B1 * a.getY(),
               C2 = A2 * c.getX() + B2 * c.getY(),
               D  = A1 * B2 - B1 * A2,
               D1 = C1 * B2 - B1 * C2,
               D2 = A1 * C2 - C1 * A2;
        if(Double.compare(D,0.) == 0) return null;

        return new Point2D.Double(D1/D,D2/D);
    }


    /**
     * finds the difference of two vectors
     *
     * @param a coordinates of the first vector
     * @param b coordinates of the second vector
     * @return the vector difference
     */
    public static Point2D vectDif(Point2D a, Point2D b){
        return new Point2D.Double(a.getX()-b.getX(), a.getY()-b.getY());
    }

    /**
     * multiplies the vector by itself
     *
     * @param a coordinates of the vector
     * @return the vector square
     */
    public static double vectSquare(Point2D a){
        return scalarProduct(a,a);
    }

    /**
     * finds the length of the vector
     *
     * @param a the coordinates of the vector
     * @return the vector length
     */
    public static double vectLength(Point2D a){
        return sqrt(scalarProduct(a,a));
    }

    /**
     * finds the scalar product of two vectors
     *
     * @param a coordinates of the first vector
     * @param b coordinates of the second vector
     * @return the scalar product
     */
    public static double scalarProduct(Point2D a, Point2D b){
        return a.getX()*b.getX() +  a.getY()*b.getY();
    }

    /**
     * finds the vector product of two vectors
     *
     * @param a coordinates of the first vector
     * @param b coordinates of the second vector
     * @return the vector product
     */
    public static double vectProduct(Point2D a, Point2D b) {
        return a.getX()*b.getY() - a.getY()*b.getX();
    }

}