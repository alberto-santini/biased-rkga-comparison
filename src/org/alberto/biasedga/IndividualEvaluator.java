package org.alberto.biasedga;

/**
 * Decodes and evaluate individuals.
 * @author alberto
 *
 */
public abstract class IndividualEvaluator {
	/**
	 * Calculates the fitness of an individual.
	 * @param individual	The individual that we need to decode and evaluate.
	 * @return				The fitness of the individual.
	 */
	public abstract double fitness_of(final Individual individual);
}
