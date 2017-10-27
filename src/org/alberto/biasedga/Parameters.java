package org.alberto.biasedga;

/**
 * Parameters for the Biased Genetic Algorithm.
 * @author alberto
 *
 */
public class Parameters {
	/**
	 * Number of individuals to keep.
	 */
	final int population_size;
	
	/**
	 * Fraction of the population that we consider elite.
	 */
	final double elite_share;
	
	/**
	 * Fraction of the population that is rebuilt from scratch at each generation.
	 */
	final double new_individuals_share;
	
	/**
	 * Bias towards the elite parent.
	 */
	final double bias;
	
	/**
	 * Maximum number of generations
	 */
	final int max_generations;
	
	/**
	 * Maximum number of consectuive generations without improvement.
	 */
	final int max_generations_no_improvement;
	
	/**
	 * Maximum running time in seconds.
	 */
	final int timeout_s;
	
	public Parameters(
			final int population_size, final double elite_share, final double new_individuals_share,
			final double bias, final int max_generations, final int max_generations_no_improvement,
			final int timeout_s
	) {
		this.population_size = population_size;
		this.elite_share = elite_share;
		this.new_individuals_share = new_individuals_share;
		this.bias = bias;
		this.max_generations = max_generations;
		this.max_generations_no_improvement = max_generations_no_improvement;
		this.timeout_s = timeout_s;
	}
}
