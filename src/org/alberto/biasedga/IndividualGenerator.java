package org.alberto.biasedga;

import java.util.Random;

/**
 * This class is a factory of new, random individuals.
 * @author alberto
 *
 */
public abstract class IndividualGenerator {
	final protected  Random prng;
	
	public IndividualGenerator() {
		this.prng = new Random();
	}
	
	public IndividualGenerator(final int random_seed) {
		this.prng = new Random(random_seed);
	}
	
	/**
	 * Generates a new individual.
	 * @return	The new, random individual generated.
	 */
	public abstract Individual generate();
}
