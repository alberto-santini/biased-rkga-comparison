package org.alberto.biasedga;

/**
 * This class keeps track of the fitness values associated with individuals.
 * @author alberto
 */
public class IndividualWithFitness implements Comparable<IndividualWithFitness> {
	/**
	 * The individual.
	 */
	final Individual individual;
	
	/**
	 * Its fitness.
	 */
	final double fitness;
	
	public IndividualWithFitness(final Individual individual, final double fitness) {
		this.individual = individual;
		this.fitness = fitness;
	}
	
	/**
	 * Asks the provided evaluator for the fitness.
	 */
	public IndividualWithFitness(final Individual individual, final IndividualEvaluator evaluator) {
		this.individual = individual;
		this.fitness = evaluator.fitness_of(individual);
	}
	
	/**
	 * Gets a new individual from the generator and asks the evaluator to evaluate its fitness.
	 */
	public IndividualWithFitness(final IndividualGenerator generator, final IndividualEvaluator evaluator) {
		this.individual = generator.generate();
		this.fitness = evaluator.fitness_of(this.individual);
	}
	
	/**
	 * Clones the individual (and the fitness).
	 */
	public IndividualWithFitness clone() {
		return new IndividualWithFitness(individual.clone(), fitness);
	}
	
	/**
	 * Comparator, which only compares the fitness.
	 */
	@Override public int compareTo(final IndividualWithFitness other) {
		return Double.compare(fitness, other.fitness);
	}
	
	public double get_fitness() { return fitness; }
}
