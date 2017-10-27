package org.alberto.tsp.ga.permutationkey;

import java.util.Random;

import org.alberto.biasedga.Individual;
import org.alberto.tsp.Graph;

/**
 * This class models a permutation-key individual for the Travelling Salesman Problem.
 * @author alberto
 *
 */
public class TSPIndividual extends Individual {
	/**
	 * Underlying graph.
	 */
	final Graph graph;
	
	/**
	 * Transposition representation of the permutation. For an array of length N,
	 * the permutation has length 2*N. To apply the permutation, swap array[permutation[0]]
	 * and array[permutation[i]]; then array[permutation[2]] and array[permutation[3]], etc.
	 */
	final int[] permutation;
	
	public TSPIndividual(final Graph graph, final int[] permutation) {
		this.graph = graph;
		this.permutation = permutation;
	}

	@Override
	public TSPIndividual clone() {
		return new TSPIndividual(graph, permutation);
	}

	@Override
	public TSPIndividual crossover(Individual other, double bias) {
		assert(other.getClass() == TSPIndividual.class);
		assert(permutation.length == ((TSPIndividual)other).permutation.length);
		
		final int[] new_perm = new int[permutation.length];
		final Random prng = new Random();
		
		for(int i = 0; i < permutation.length; i += 2) {
			if(prng.nextDouble() < bias) {
				new_perm[i] = permutation[i];
				new_perm[i+1] = permutation[i+1];
			} else {
				new_perm[i] = ((TSPIndividual)other).permutation[i];
				new_perm[i+1] = ((TSPIndividual)other).permutation[i+1];
			}
		}
		
		return new TSPIndividual(graph, new_perm);
	}
}
