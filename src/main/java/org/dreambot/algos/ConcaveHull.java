package org.dreambot.algos;

import javafx.util.Pair;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * ConcaveHull.java - 14/10/16
 *
 * @author Udo Schlegel - Udo.3.Schlegel(at)uni-konstanz.de
 * @version 1.0
 *
 * This is an implementation of the algorithm described by Adriano Moreira and Maribel Yasmina Santos:
 * CONCAVE HULL: A K-NEAREST NEIGHBOURS APPROACH FOR THE COMPUTATION OF THE REGION OCCUPIED BY A SET OF PS.
 * GRAPP 2007 - International Conference on Computer Graphics Theory and Applications; pp 61-68.
 *
 * https://repositorium.sdum.uminho.pt/bitstream/1822/6429/1/ConcaveHull_ACM_MYS.pdf
 *
 * With help from https://github.com/detlevn/QGIS-ConcaveHull-Plugin/blob/master/concavehull.py
 */
public class ConcaveHull {

    public static class P {

        private final Double x;
        private final Double y;

        public P(Double x, Double y) {
            this.x = x;
            this.y = y;
        }

        public Double getX() {
            return x;
        }

        public Double getY() {
            return y;
        }

        public String toString() {
            return "(" + x + " " + y + ")";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof P) {
                if (x.equals(((P) obj).getX()) && y.equals(((P) obj).getY())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            // http://stackoverflow.com/questions/22826326/good-hashcode-function-for-2d-coordinates
            // http://www.cs.upc.edu/~alvarez/calculabilitat/enumerabilitat.pdf
            int tmp = (int) (y + ((x + 1) / 2));
            return Math.abs((int) (x + (tmp * tmp)));
        }
    }

    private <P extends Point> Double euclideanDistance(P a, P b) {
        return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    }

    private <P extends Point>  ArrayList<P> kNearestNeighbors(ArrayList<P> l, P q, Integer k) {
        ArrayList<Pair<Double, P>> nearestList = new ArrayList<>();
        for (P o : l) {
            nearestList.add(new Pair<>(euclideanDistance(q, o), o));
        }

        Collections.sort(nearestList, new Comparator<Pair<Double, P>>() {
            @Override
            public int compare(Pair<Double, P> o1, Pair<Double, P> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });

        ArrayList<P> result = new ArrayList<>();

        for (int i = 0; i < Math.min(k, nearestList.size()); i++) {
            result.add(nearestList.get(i).getValue());
        }

        return result;
    }

    private <P extends Point> P findMinYP(ArrayList<P> l) {
        Collections.sort(l, new Comparator<P>() {
            @Override
            public int compare(P o1, P o2) {
                return Double.compare(o1.getY(), o2.getY());
            }
        });
        return l.get(0);
    }

    private <P extends Point>  Double calculateAngle(P o1, P o2) {
        return Math.atan2(o2.getY() - o1.getY(), o2.getX() - o1.getX());
    }

    private Double angleDifference(Double a1, Double a2) {
        // calculate angle difference in clockwise directions as radians
        if ((a1 > 0 && a2 >= 0) && a1 > a2) {
            return Math.abs(a1 - a2);
        } else if ((a1 >= 0 && a2 > 0) && a1 < a2) {
            return 2 * Math.PI + a1 - a2;
        } else if ((a1 < 0 && a2 <= 0) && a1 < a2) {
            return 2 * Math.PI + a1 + Math.abs(a2);
        } else if ((a1 <= 0 && a2 < 0) && a1 > a2) {
            return Math.abs(a1 - a2);
        } else if (a1 <= 0 && 0 < a2) {
            return 2 * Math.PI + a1 - a2;
        } else if (a1 >= 0 && 0 >= a2) {
            return a1 + Math.abs(a2);
        } else {
            return 0.0;
        }
    }

    private <P extends Point> ArrayList<P> sortByAngle(ArrayList<P> l, P q, Double a) {
        // Sort by angle descending
        Collections.sort(l, new Comparator<P>() {
            @Override
            public int compare(final P o1, final P o2) {
                Double a1 = angleDifference(a, calculateAngle(q, o1));
                Double a2 = angleDifference(a, calculateAngle(q, o2));
                return a2.compareTo(a1);
            }
        });
        return l;
    }

    private <P extends Point> Boolean intersect(P l1p1, P l1p2, P l2p1, P l2p2) {
        // calculate part equations for line-line intersection
        Double a1 = l1p2.getY() - l1p1.getY();
        Double b1 = l1p1.getX() - l1p2.getX();
        Double c1 = a1 * l1p1.getX() + b1 * l1p1.getY();
        Double a2 = l2p2.getY() - l2p1.getY();
        Double b2 = l2p1.getX() - l2p2.getX();
        Double c2 = a2 * l2p1.getX() + b2 * l2p1.getY();
        // calculate the divisor
        Double tmp = (a1 * b2 - a2 * b1);

        // calculate intersection P x coordinate
        Double pX = (c1 * b2 - c2 * b1) / tmp;

        // check if intersection x coordinate lies in line line segment
        if ((pX > l1p1.getX() && pX > l1p2.getX()) || (pX > l2p1.getX() && pX > l2p2.getX())
                || (pX < l1p1.getX() && pX < l1p2.getX()) || (pX < l2p1.getX() && pX < l2p2.getX())) {
            return false;
        }

        // calculate intersection P y coordinate
        Double pY = (a1 * c2 - a2 * c1) / tmp;

        // check if intersection y coordinate lies in line line segment
        if ((pY > l1p1.getY() && pY > l1p2.getY()) || (pY > l2p1.getY() && pY > l2p2.getY())
                || (pY < l1p1.getY() && pY < l1p2.getY()) || (pY < l2p1.getY() && pY < l2p2.getY())) {
            return false;
        }

        return true;
    }

    private <P extends Point> boolean PInPolygon(P p, ArrayList<P> pp) {
        boolean result = false;
        for (int i = 0, j = pp.size() - 1; i < pp.size(); j = i++) {
            if ((pp.get(i).getY() > p.getY()) != (pp.get(j).getY() > p.getY()) &&
                    (p.getX() < (pp.get(j).getX() - pp.get(i).getX()) * (p.getY() - pp.get(i).getY()) / (pp.get(j).getY() - pp.get(i).getY()) + pp.get(i).getX())) {
                result = !result;
            }
        }
        return result;
    }

    public ConcaveHull() {

    }

    public <P extends Point> List<P> calculateConcaveHull(List<P> PArrayList, Integer k) {

        // the resulting concave hull
        ArrayList<P> concaveHull = new ArrayList<>();

        // optional remove duplicates
        HashSet<P> set = new HashSet<>(PArrayList);
        ArrayList<P> PArraySet = new ArrayList<>(set);

        // k has to be greater than 3 to execute the algorithm
        Integer kk = Math.max(k, 3);

        // return Ps if already Concave Hull
        if (PArraySet.size() < 3) {
            return PArraySet;
        }

        // make sure that k neighbors can be found
        kk = Math.min(kk, PArraySet.size() - 1);

        // find first P and remove from P list
        P firstP = findMinYP(PArraySet);
        concaveHull.add(firstP);
        P currentP = firstP;
        PArraySet.remove(firstP);

        Double previousAngle = 0.0;
        Integer step = 2;

        while ((currentP != firstP || step == 2) && PArraySet.size() > 0) {

            // after 3 steps add first P to dataset, otherwise hull cannot be closed
            if (step == 5) {
                PArraySet.add(firstP);
            }

            // get k nearest neighbors of current P
            ArrayList<P> kNearestPs = kNearestNeighbors(PArraySet, currentP, kk);

            // sort Ps by angle clockwise
            ArrayList<P> clockwisePs = sortByAngle(kNearestPs, currentP, previousAngle);

            // check if clockwise angle nearest neighbors are candidates for concave hull
            Boolean its = true;
            int i = -1;
            while (its && i < clockwisePs.size() - 1) {
                i++;

                int lastP = 0;
                if (clockwisePs.get(i) == firstP) {
                    lastP = 1;
                }

                // check if possible new concave hull P intersects with others
                int j = 2;
                its = false;
                while (!its && j < concaveHull.size() - lastP) {
                    its = intersect(concaveHull.get(step - 2), clockwisePs.get(i), concaveHull.get(step - 2 - j), concaveHull.get(step - 1 - j));
                    j++;
                }
            }

            // if there is no candidate increase k - try again
            if (its) {
                return calculateConcaveHull(PArrayList, k + 1);
            }

            // add candidate to concave hull and remove from dataset
            currentP = clockwisePs.get(i);
            concaveHull.add(currentP);
            PArraySet.remove(currentP);

            // calculate last angle of the concave hull line
            previousAngle = calculateAngle(concaveHull.get(step - 1), concaveHull.get(step - 2));

            step++;

        }

        // Check if all Ps are contained in the concave hull
        Boolean insideCheck = true;
        int i = PArraySet.size() - 1;

        while (insideCheck && i > 0) {
            insideCheck = PInPolygon(PArraySet.get(i), concaveHull);
            i--;
        }

        // if not all Ps inside -  try again
        if (!insideCheck) {
            return calculateConcaveHull(PArrayList, k + 1);
        } else {
            return concaveHull;
        }

    }

}

