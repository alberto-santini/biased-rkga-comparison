package org.alberto.tsp.ga.randomkey;

import java.util.stream.IntStream;

import org.alberto.biasedga.IndividualGenerator;
import org.alberto.tsp.Graph;

/**
 * This class is used to generate random random-key individuals for the
 * Travelling Salesman Problem.
 * @author alberto
 *
 */
public class TSPIndividualGenerator extends IndividualGenerator {
	/**
	 * Underlying graph for the TSP.
	 */
	final Graph graph;
	
	public TSPIndividualGenerator(final Graph graph) {
		super();
		this.graph = graph;
	}

	public TSPIndividualGenerator(final Graph graph, final int random_seed) {
		super(random_seed);
		this.graph = graph;
	}

	@Override
	public TSPIndividual generate() {
		final double[] keys = new double[graph.nodes_num()];
		IntStream.rangeClosed(0, graph.nodes_num() - 1).forEach(i -> keys[i] = prng.nextDouble());
		return new TSPIndividual(graph, keys);
	}
}