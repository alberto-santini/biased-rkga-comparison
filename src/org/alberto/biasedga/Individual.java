package org.alberto.biasedga;

/**
 * Building block of the genetic algorithm.
 * @author alberto
 *
 */
public abstract class Individual {
	/**
	 * Makes a copy of itself.
	 */
	public abstract Individual clone();
	
	/**
	 * Crosses this individual with another one, with a certain bias for the current individual.
	 */
	public abstract Individual crossover(final Individual other, final double bias);
}
