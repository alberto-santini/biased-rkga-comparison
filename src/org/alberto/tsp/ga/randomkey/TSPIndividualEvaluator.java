package org.alberto.tsp.ga.randomkey;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.alberto.biasedga.Individual;
import org.alberto.biasedga.IndividualEvaluator;
import org.alberto.tsp.Graph;

/**
 * This class implements an evaluator for the random-key individuals for
 * the Travelling Salesman Problem.
 * @author alberto
 *
 */
public class TSPIndividualEvaluator extends IndividualEvaluator {
	/**
	 * The underlying TSP graph.
	 */
	final Graph graph;

	public TSPIndividualEvaluator(final Graph graph) {
		this.graph = graph;
	}

	@Override
	public double fitness_of(Individual individual) {
		assert(individual.getClass() == TSPIndividual.class);
		
		final double[] keys = ((TSPIndividual)individual).keys;
		final List<Integer> indices = IntStream.rangeClosed(0, keys.length - 1).boxed().collect(Collectors.toList());
		
		Collections.sort(indices, (i1, i2) -> Double.compare(keys[i1], keys[i2]));
		
		double fitness = 0;
		
		for(int i = 0; i < indices.size() - 1; i++) {
			fitness += graph.get_distance(indices.get(i), indices.get(i+1));
		}
		
		fitness += graph.get_distance(indices.get(indices.size() - 1), indices.get(0));
		
		return fitness;
	}
}
