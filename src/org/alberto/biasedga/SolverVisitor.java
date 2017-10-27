package org.alberto.biasedga;

/**
 * This class implements a visitor for the solver. The visitor's actions are
 * called by the Solver, at particular times during the solution process. 
 * @author santinia
 *
 */
public abstract class SolverVisitor {
	/**
	 * The solver should call the ``at_fixed_number_of_iterations'' actions every
	 * ``observe_every_n_iterations'' iterations.
	 */
	final int observe_every_n_iterations;
	
	public SolverVisitor() {
		this.observe_every_n_iterations = Integer.MAX_VALUE;
	}
	
	public SolverVisitor(int observe_every_n_iterations) {
		this.observe_every_n_iterations = observe_every_n_iterations;
	}
	
	/**
	 * This observer action is called by the solver every ``observe_every_n_iterations''
	 * iterations.
	 */
	public abstract void at_fixed_number_of_iterations(final Solver solver, final int iteration, final double elapsed_time_s);
	
	/**
	 * This observer action is called by the solver when the solution process is over.
	 */
	public abstract void at_end(final Solver solver, final int iteration, final double elapsed_time_s);
}
