package org.alberto.biasedga;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Solver for the Biased Genetic Algorithm.
 * @author alberto
 *
 */
public class Solver {	
	/**
	 * Solver parameters.
	 */
	final Parameters params;
	
	/**
	 * Factory of new, random individuals.
	 */
	final IndividualGenerator generator;
	
	/**
	 * Evaluator that takes an individual and spits out its fitness.
	 */
	final IndividualEvaluator evaluator;
	
	/**
	 * Visitor to be called by the solver when important events happen.
	 */
	final SolverVisitor visitor;
	
	/**
	 * Population, i.e. list of individuals -- which we conveniently keep sorted by fitness.
	 * Please notice that fitness is an apt name only in case of maximisation problems. In case
	 * of minimisation problems, we actually prefer the individuals with the lowest ``fitness''.
	 * If you want to resolve this ambiguity, have your IndividualEvaluator return, e.g., the
	 * negative objective value, when you have a minimisation problem.
	 */
	SortedSet<IndividualWithFitness> population;
	
	/**
	 * Number of elite individuals in the population.
	 */
	final int elite_size;
	
	/**
	 * Number of new ``mutants'' introduced at each generation.
	 */
	final int new_individuals_size;
	
	/**
	 * Number of concurrent threads we can use for parallel decoding of individuals.
	 */
	final static int N_THREADS = 4;
	
	public Solver(final Parameters params, final IndividualGenerator generator, final IndividualEvaluator evaluator, final SolverVisitor visitor) {
		this.params = params;
		this.generator = generator;
		this.evaluator = evaluator;
		this.visitor = visitor;
		this.population = new TreeSet<IndividualWithFitness>();
		this.elite_size = (int) (params.population_size * params.elite_share);
		this.new_individuals_size = (int) (params.population_size * params.new_individuals_share);
	}
	
	public IndividualWithFitness solve() {
		int generation = 0;
		int generations_no_improvement = 0;
		long start_time = System.nanoTime();
		
		double eps = 1e-6;
		
		// Create the initial population
		initialise_population();
				
		while(generation < params.max_generations && generations_no_improvement < params.max_generations_no_improvement) {
			long current_time = System.nanoTime();
			double elapsed_time_s = (current_time - start_time) / 10e9;
			
			if(elapsed_time_s > params.timeout_s) { break; }
			
			// Evolve a new generation
			SortedSet<IndividualWithFitness> new_gen = evolve_new_generation();
			
			// Check whether there has been a (strictly positive) improvement
			if(new_gen.first().fitness >= population.first().fitness - eps) { generations_no_improvement++; } else { generations_no_improvement = 0; }
			
			// Replace the old population with the new generation
			population = new_gen;
			
			// Call the visitor every now and then
			if(generation % visitor.observe_every_n_iterations == 0) {
				visitor.at_fixed_number_of_iterations(this, generation, elapsed_time_s);
			}
			
			generation++;
		}
		
		long end_time = System.nanoTime();
		double total_time_s = (end_time - start_time) / 10e9;
		
		// Call the visitor for the end action
		visitor.at_end(this, generation, total_time_s);
		
		// Return the best individual in the population
		// N.B. this is the best individual ever encountered: since
		// we copy the elite members, there is no risk that the best
		// individual in a generation is not included in the next one
		return population.first();
	}
	
	/**
	 * Initialises the population with population_size new random individuals.
	 */
	void initialise_population() {
		IntStream.rangeClosed(1, params.population_size).forEach(i -> population.add(new IndividualWithFitness(generator, evaluator)));
	}
	
	/**
	 * Evolves the new generation, putting in it:
	 * - The elite individuals in the current population;
	 * - The new ``mutant'' individuals;
	 * - The individuals generated by crossover.
	 * @return The new generation's population
	 */
	SortedSet<IndividualWithFitness> evolve_new_generation() {
		final SortedSet<IndividualWithFitness> new_gen = new TreeSet<IndividualWithFitness>();
		
		// Keep track of the first non-elite individual
		IndividualWithFitness first_non_elite = null;
		
		// Insert elite
		int elite_inserted = 0;
		for(final IndividualWithFitness individual : population) {
			// If this is true, we are considering the first non-elite individual
			if(elite_inserted == elite_size) { first_non_elite = individual; break; }
			
			new_gen.add(individual.clone());
			elite_inserted++;
		}
				
		assert(first_non_elite != null);
		
		// Insert new mutants in parallel
		insert_mutants(new_gen);
		
		// Fill the population with individuals generated by cross-over
		do_xover(new_gen, first_non_elite);

		return new_gen;
	}
	
	/**
	 * Inserts the new mutant individuals in the new generation's population.
	 * @param new_gen The (new) population into which to insert the new mutants
	 */
	void insert_mutants(final SortedSet<IndividualWithFitness> new_gen) {
		final Object lock = new Object();
		
		// Insert new mutants in parallel
		final ExecutorService es = Executors.newFixedThreadPool(N_THREADS);
		final List<Callable<Object>> callmutants = new ArrayList<Callable<Object>>();
		for(int i = 0; i < new_individuals_size; i++) {
			callmutants.add(Executors.callable(
					new Runnable() {
						@Override public void run() {
							final IndividualWithFitness ind = new IndividualWithFitness(generator, evaluator);
							synchronized(lock) { new_gen.add(ind); }
						}
					}
			));
		}
		
		try { es.invokeAll(callmutants); } catch (InterruptedException e) { System.err.println("Fatal error while generating mutants."); System.exit(1); }
		es.shutdown();
	}
	
	/**
	 * Completes the new generation's population by doing cross-over between one elite and one non-elite parent.
	 * @param new_gen 			The (new) population to complete
	 * @param first_non_elite	The first non-elite individual in the current (old) population
	 */
	void do_xover(final SortedSet<IndividualWithFitness> new_gen, final IndividualWithFitness first_non_elite) {
		final Object lock = new Object();
		
		// Get a list of elite individuals
		final List<Individual> elite = population
				.headSet(first_non_elite)		// Get the IndividualWithFitness objects better than first_non_elite
				.stream()						// Make a stream out of them
				.map(i -> i.individual)			// Get the Individual out of IndividualWithFitness
				.collect(Collectors.toList());  // Collect the individuals
		
		// Get a list of non-elite individuals
		final List<Individual> normal = population
				.tailSet(first_non_elite)		// Get the IndividualWithFitness objects worse or equal than first_non_elite
				.stream()						// Make a stream out of them
				.map(i -> i.individual)			// Get the Individual out of IndividualWithFitness
				.collect(Collectors.toList());	// Collect the individuals
		
		// Keep crossing over until we reach the desired population size, in parallel
		final ExecutorService es = Executors.newFixedThreadPool(N_THREADS);
		final List<Callable<Object>> callxover = new ArrayList<Callable<Object>>();
		final int n_crossover_individuals = params.population_size - new_gen.size();
		for(int i = 0; i < n_crossover_individuals; i++) {
			callxover.add(Executors.callable(
					new Runnable() {
						@Override public void run() {
							final int elite_id = ThreadLocalRandom.current().nextInt(elite.size());
							final int normal_id = ThreadLocalRandom.current().nextInt(normal.size());
							
							// Generate the child from biased crossover between elite and non-elite parents
							final Individual child = elite.get(elite_id).crossover(normal.get(normal_id), params.bias);
							final IndividualWithFitness ind = new IndividualWithFitness(child, evaluator);
							synchronized(lock) { new_gen.add(ind); }
						}
					}
			));
		}
		
		try { es.invokeAll(callxover); } catch (InterruptedException e) { System.err.println("Fatal error while doing cross-over."); System.exit(1); }
		es.shutdown();
	}
}