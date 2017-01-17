package org.dreambot.algos.search.peucker;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RamerDouglasPeucker {
	
	// http://bost.ocks.org/mike/simplify/
	// https://en.wikipedia.org/wiki/Ramer%E2%80%93Douglas%E2%80%93Peucker_algorithm

	static public <P extends Point> List<P> process(List<P> vertices, Double distanceThreshold) {
		List<P> result = null;

		if (vertices != null) {
			result = simplificationOf(vertices, distanceThreshold);
		}

		return result;
	}

	private static <P extends Point> List<P> simplificationOf(List<P> vertices, Double distanceThreshold) {
		List<P> simplifiedVertices = new ArrayList<>();

		vertices.removeIf(Objects::isNull);

		Double maxDistance = null;
		int maxDistancePointIdx = 0;

		int lastPointIdx = vertices.size()-1;

		int currentIdx = 0;
		for (Point aVertex : vertices) {

			if (currentIdx != 0 && currentIdx != lastPointIdx) {

				Double distance = shortestDistanceToSegment(aVertex, vertices.get(0), vertices.get(lastPointIdx));
				//System.out.println("aVertex: " + aVertex + ", segment[" + vertices.get(0) + ", " + vertices.get(lastPointIdx) + "] -> distance: " + distance);
				if (maxDistance == null || distance > maxDistance) {
					maxDistancePointIdx = currentIdx;
					maxDistance = distance;
				}

			}
			currentIdx++;
		}

		if (maxDistance != null) {
			if (maxDistance > distanceThreshold) {
				List<P> sub = RamerDouglasPeucker.process(vertices.subList(0, maxDistancePointIdx+1), distanceThreshold);
				List<P> sup = RamerDouglasPeucker.process(vertices.subList(maxDistancePointIdx, lastPointIdx+1), distanceThreshold);

				simplifiedVertices.addAll(sub);
				simplifiedVertices.addAll(sup);

			} else {
				simplifiedVertices.add(vertices.get(0));
				simplifiedVertices.add(vertices.get(lastPointIdx));
			}
		}
		return simplifiedVertices;
	}

	static Double shortestDistanceToSegment(Point thePoint,Point segmentPoint_A, Point segmentPoint_B) {
		Double area = calculateTriangleAreaGivenVertices(thePoint, segmentPoint_A, segmentPoint_B);
		Double lengthSegment = calculateDistanceBetweenTwoPoints(segmentPoint_A, segmentPoint_B);
		return (2 * area) / lengthSegment;
	}

	static Double calculateTriangleAreaGivenVertices(Point a, Point b, Point c) {
		// http://www.mathopenref.com/coordtrianglearea.html
		Double area = Math.abs(((a.getX() * (b.getY() - c.getY())) + (b.getX() * (c.getY() - a.getY())) + (c.getX() * (a.getY() - b.getY()))) / 2);
		return area;
	}

	static Double calculateDistanceBetweenTwoPoints(Point a, Point b) {
		Double distance = Math.sqrt(((a.getX() - b.getX()) * (a.getX() - b.getX())) + ((a.getY() - b.getY()) * (a.getY() - b.getY())));
		return distance;
	}
}