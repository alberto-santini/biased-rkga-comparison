package org.alberto.tsp.ga.permutationkey;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.alberto.biasedga.Individual;
import org.alberto.biasedga.IndividualEvaluator;
import org.alberto.tsp.Graph;

/**
 * This class implements an evaluator for the permutation-key individuals for
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
		
		final int[] permutation = ((TSPIndividual) individual).permutation;
		final List<Integer> tour = IntStream.rangeClosed(0, (permutation.length / 2) - 1).boxed().collect(Collectors.toList());
		
		for(int i = 0; i < permutation.length; i += 2) {
			Collections.swap(tour, permutation[i], permutation[i+1]);
		}
				
		double fitness = 0;
		
		for(int i = 0; i < tour.size() - 1; i++) {
			fitness += graph.get_distance(tour.get(i), tour.get(i+1));
		}
		
		fitness += graph.get_distance(tour.get(tour.size() - 1), tour.get(0));
		
		return fitness;
	}
}
