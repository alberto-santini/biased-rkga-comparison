package org.alberto.tsp;

import java.util.Random;

/**
 * This is a generator for random graphs with euclidean distances.
 * @author alberto
 *
 */
public class EuclideanGraphGenerator {
	/**
	 * Number of nodes.
	 */
	final int nodes_num;
	
	/**
	 * Maximum distance between nodes.
	 */
	final double max_distance;
	
	/**
	 * Pseudo-random number generator.
	 */
	final Random prng;
	
	public EuclideanGraphGenerator(final int nodes_num, final int max_distance) {
		this.nodes_num = nodes_num;
		this.max_distance = max_distance;
		this.prng = new Random();
	}
	
	/**
	 * Generates a graph with nodes_num vertices, chosen at random inside a circle
	 * of radius max_distance / 2.
	 */
	public Graph generate() {
		class Point { final double x; final double y; public Point(final double x, final double y) { this.x = x; this.y = y; } }
		final Point[] points = new Point[nodes_num];
		
		for(int i = 0; i < nodes_num; i++) {
			final double radius = Math.sqrt(prng.nextDouble() * max_distance / 2);
			final double angle = prng.nextDouble() * 2 * Math.PI;
			points[i] = new Point(radius * Math.cos(angle), radius * Math.sin(angle));
		}
		
		final double[][] distance = new double[nodes_num][nodes_num];
		
		for(int i = 0; i < nodes_num; i++) {
			distance[i][i] = 0;
			for(int j = i + 1; j < nodes_num; j++) {
				final double d = Math.sqrt(Math.pow(points[i].x - points[j].x, 2) + Math.pow(points[i].y - points[j].y, 2));
				distance[i][j] = d;
				distance[j][i] = d;
			}
		}
		
		return new Graph(distance);
	}
}
