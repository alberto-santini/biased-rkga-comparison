package org.alberto.tsp.ga.randomkey;

import java.util.Random;

import org.alberto.biasedga.Individual;
import org.alberto.tsp.Graph;

/**
 * This class models a random-key individual for the Travelling Salesman Problem.
 * @author alberto
 *
 */
public class TSPIndividual extends Individual {
	/**
	 * Underlying graph.
	 */
	final Graph graph;
	
	/**
	 * Genetic representation with random keys.
	 */
	final double[] keys;
	
	public TSPIndividual(final Graph graph, final double keys[]) {
		this.graph = graph;
		this.keys = keys;
	}

	@Override
	public Individual clone() {
		return new TSPIndividual(graph, keys);
	}

	@Override
	public TSPIndividual crossover(Individual other, double bias) {
		assert(other.getClass() == TSPIndividual.class);
		assert(keys.length == ((TSPIndividual)other).keys.length);
		
		final double[] new_keys = new double[keys.length];
		final Random prng = new Random();
		
		for(int i = 0; i < keys.length; i++) {
			if(prng.nextDouble() < bias) {
				new_keys[i] = keys[i];
			} else {
				new_keys[i] = ((TSPIndividual)other).keys[i];
			}
		}
		
		return new TSPIndividual(graph, new_keys);
	}
}
