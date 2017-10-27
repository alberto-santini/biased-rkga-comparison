package org.alberto.biasedga;

/**
 * Convenient builder class for Parameters.
 * @author alberto
 *
 */
public class ParametersBuilder {
	int population_size = 100;
	double elite_share = 0.1;
	double new_individuals_share = 0.1;
	double bias = 0.7;
	int max_generations = Integer.MAX_VALUE;
	int max_generations_no_improvement = Integer.MAX_VALUE;
	int timeout_s = Integer.MAX_VALUE;
	
	public ParametersBuilder with_population_size(final int population_size) { this.population_size = population_size; return this; }
	public ParametersBuilder with_elite_share(final double elite_share) { this.elite_share = elite_share; return this; }
	public ParametersBuilder with_new_individuals_share(final double new_individuals_share) { this.new_individuals_share = new_individuals_share; return this; }
	public ParametersBuilder with_bias(final double bias) { this.bias = bias; return this; }
	public ParametersBuilder with_max_generations(final int max_generations) { this.max_generations = max_generations; return this; }
	public ParametersBuilder with_max_generations_no_improvement(final int max_generations_no_improvement) { this.max_generations_no_improvement = max_generations_no_improvement; return this; }
	public ParametersBuilder with_timeout_s(final int timeout_s) { this.timeout_s = timeout_s; return this; }
	public Parameters build() { return new Parameters(population_size, elite_share, new_individuals_share, bias, max_generations, max_generations_no_improvement, timeout_s); }
}
