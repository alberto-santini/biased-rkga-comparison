package org.alberto.tsp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This a generator for graphs from TSPLib instances.
 * @author santinia
 *
 */
public class TSPLibGraphGenerator {
	/**
	 * TSPLib file name.
	 */
	final String filename;
	
	public TSPLibGraphGenerator(final String filename) {
		this.filename = filename;
	}
	
	public Graph generate() {
		try(Stream<String> stream = Files.lines(Paths.get(filename))) {
			final List<Integer> distances = stream
					.map(str -> str.split(" "))						// Split each line by spaces
					.flatMap(ary -> Arrays.asList(ary).stream())	// Flatten the list of lists
					.filter(str -> !str.isEmpty()) 					// Remove empty strings
					.map(Integer::parseInt)							// Parse each entry as an integer
					.collect(Collectors.toList());					// Put everything in one list
			
			final int num_nodes = distances.get(0);
			final double[][] distmatrix = new double[num_nodes][num_nodes];
			
			int current = 1;
			for(int i = 0; i < num_nodes; i++) {
				for(int j = 0; j <= i; j++) {
					distmatrix[i][j] = distances.get(current);
					distmatrix[j][i] = distances.get(current);
					current++;
				}
			}
			
			return new Graph(distmatrix);
		} catch(IOException e) {
			System.err.println("Cannot read TSPLib data file: " + filename);
			System.exit(1);
			return new Graph(new double[0][0]); // Cheat java into thinking we are returning something.
		}
	}
}
