package org.alberto.tsp.solver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.alberto.biasedga.DefaultSolverVisitor;
import org.alberto.biasedga.IndividualWithFitness;
import org.alberto.biasedga.Parameters;
import org.alberto.biasedga.ParametersBuilder;
import org.alberto.biasedga.Solver;
import org.alberto.biasedga.SolverVisitor;
import org.alberto.tsp.Graph;
import org.alberto.tsp.TSPLibGraphGenerator;

/**
 * This class is used to test the Biased GA solvers for the TSP.
 * In particular, it will test the "Random Key" and "Permutation Key"
 * approaches, on a small number of TSPLib instances.
 * @author santinia
 *
 */
public class TestSolver {
	public static void main(String[] args) {
		final String instance_folder = "tsplib";
		final String[] instances = { "gr120", "gr17", "gr21", "gr24", "gr48", "hk48", "pa561" };
		final String instance_ext = ".tsp";
		final String results_file = "results.csv";
		
		try(final PrintWriter results_writer = new PrintWriter(new FileOutputStream(results_file), true)) {
			results_writer.println("instance,key,avg,stddev");
			
			// Number of reruns to perform, for each instance and each method.
			final int reruns = 5;
			
			// Parameters common to all runs
			final Parameters params = new ParametersBuilder()
					.with_population_size(150)
					.with_bias(0.7)
					.with_elite_share(0.1)
					.with_new_individuals_share(0.1)
					.with_timeout_s(60)
					.build();
			
			// Joins parts of a path in a portable way ("\" on Win, "/" on Unix, etc.)
			final BiFunction<String, String, String> combine_path =
					(firstpart, lastpart) -> new File(new File(firstpart), lastpart).getPath();
					
			for(final String instance : instances) {
				System.out.println("Tests for instance: " + instance);
				
				// Read the instance and build the graph
				final String instance_path = combine_path.apply(instance_folder, instance + instance_ext);
				final TSPLibGraphGenerator gen = new TSPLibGraphGenerator(instance_path);
				final Graph graph = gen.generate();
				
				// Vectors where to collect the data across reruns
				final List<Double> rk_results = new ArrayList<Double>(); // Random-key
				final List<Double> pk_results = new ArrayList<Double>(); // Permutation-key
				
				// Calculates the avg of a vector of doubles
				final Function<List<Double>, Double> get_avg =
						(ary) -> ary.stream().mapToDouble(Double::doubleValue).sum() / ary.size();
				// Calculates the stddev of a vector of doubles, given the avg
				final BiFunction<List<Double>, Double, Double> get_dev =
						(ary, avg) -> Math.sqrt(ary.stream().mapToDouble(Double::doubleValue).map(x -> Math.pow(x - avg, 2)).sum() / (ary.size() - 1));
				
				// Perform the test ``reruns'' times:
				for(int run = 0; run < reruns; run++) {
					System.out.println("Rerun #" + run);
					
					System.out.println("Launching the random-key agorithm");
					// 1) Test the Random-Key algorithm
					final org.alberto.tsp.ga.randomkey.TSPIndividualGenerator rk_generator =
							new org.alberto.tsp.ga.randomkey.TSPIndividualGenerator(graph);
					final org.alberto.tsp.ga.randomkey.TSPIndividualEvaluator rk_evaluator =
							new org.alberto.tsp.ga.randomkey.TSPIndividualEvaluator(graph);
					final SolverVisitor rk_visitor = new DefaultSolverVisitor("randomkey-" + instance + "-run-" + run + "-log.csv");
					
					final Solver rk_solver = new Solver(params, rk_generator, rk_evaluator, rk_visitor);
					final IndividualWithFitness rk_best = rk_solver.solve();
					rk_results.add(rk_best.get_fitness());
					
					System.out.println("Launching the permutation-key agorithm");
					// 2) Test the Permutation-Key algorithm
					final org.alberto.tsp.ga.permutationkey.TSPIndividualGenerator pk_generator =
							new org.alberto.tsp.ga.permutationkey.TSPIndividualGenerator(graph);
					final org.alberto.tsp.ga.permutationkey.TSPIndividualEvaluator pk_evaluator =
							new org.alberto.tsp.ga.permutationkey.TSPIndividualEvaluator(graph);
					final SolverVisitor pk_visitor = new DefaultSolverVisitor("permutationkey-" + instance + "-run-" + run + "-log.csv");
					
					final Solver pk_solver = new Solver(params, pk_generator, pk_evaluator, pk_visitor);
					final IndividualWithFitness pk_best = pk_solver.solve();
					pk_results.add(pk_best.get_fitness());
				}
				
				// Add results to file:
				final double rk_avg = get_avg.apply(rk_results);
				final double rk_dev = get_dev.apply(rk_results, rk_avg);
				results_writer.println(instance + ",random," + rk_avg + "," + rk_dev);
				final double pk_avg = get_avg.apply(pk_results);
				final double pk_dev = get_dev.apply(pk_results, pk_avg);
				results_writer.println(instance + ",permutation," + pk_avg + "," + pk_dev);
			}
		} catch(final IOException e) {
			System.err.println("Cannot open results file: " + results_file);
			System.exit(1);
		}
	}
}
