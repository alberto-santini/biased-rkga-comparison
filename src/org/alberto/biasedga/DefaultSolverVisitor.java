package org.alberto.biasedga;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Default visitor for the solver, which just prints stats to file
 * every once in a while, in CSV format.
 * @author santinia
 *
 */
public class DefaultSolverVisitor extends SolverVisitor {
	/**
	 * Name of the file where to save the stats.
	 */
	final String filename;
	
	/**
	 * File writer to be used by the visitor.
	 */
	final PrintWriter writer;

	public DefaultSolverVisitor(final String filename) {
		super(1000);
		this.filename = filename;
		this.writer = initialise_writer(filename);
		
		writer.print("iteration,time,value\n");
	}
	
	@Override
	public void at_fixed_number_of_iterations(final Solver solver, final int iteration, final double elapsed_time_s) {
		if(writer != null) { writer.print("" + iteration + "," + elapsed_time_s + "," + solver.population.first().fitness + "\n"); }
	}
	
	@Override
	public void at_end(final Solver solver, final int iteration, final double elapsed_time_s) {
		if(writer != null) {
			System.out.println("Terminating after " + iteration + " generations and " + elapsed_time_s + " seconds.");
			writer.close();
		}
	}

	/**
	 * Try to initialise a PrintWriter to write in ``filename''.
	 */
	PrintWriter initialise_writer(String filename) {
		try {
			return new PrintWriter(new FileOutputStream(filename), true);
		} catch(IOException e) {
			System.err.println("Could not open output file for stats: " + filename);
			System.exit(1);
			return null;
		}
	}
}
