package org.alberto.tsp.ga.permutationkey;

import java.util.stream.IntStream;

import org.alberto.biasedga.IndividualGenerator;
import org.alberto.tsp.Graph;

/**
 * This class is used to generate random permutation-key individuals for the
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
		final int n = graph.nodes_num();
		final int[] permutation = new int[2 * n];
		IntStream.rangeClosed(0, n - 1).forEach(i -> permutation[i] = prng.nextInt(n - 1));
		return new TSPIndividual(graph, permutation);
	}
}
