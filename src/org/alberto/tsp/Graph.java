package org.alberto.tsp;

/**
 * This class models a simple graph. The graph is complete and you can only
 * check the distance between the members.
 * @author alberto
 *
 */
public class Graph {
	/**
	 * Distance matrix.
	 */
	final double[][] distance;
	
	public Graph(final double[][] distance) {
		this.distance = distance;
	}
	
	public int nodes_num() {
		return distance.length;
	}
	
	public double get_distance(final int i, final int j) {
		assert(0 <= i && i < distance.length);
		assert(0 <= j && j < distance.length);
		return distance[i][j];
	}
}
