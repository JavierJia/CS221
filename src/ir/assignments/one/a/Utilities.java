package ir.assignments.one.a;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A collection of utility methods for text processing.
 */
public class Utilities {
	/**
	 * Reads the input text file and splits it into alphanumeric tokens.
	 * Returns an ArrayList of these tokens, ordered according to their
	 * occurrence in the original text file.
	 * 
	 * Non-alphanumeric characters delineate tokens, and are discarded.
	 *
	 * Words are also normalized to lower case. 
	 * 
	 * Example:
	 * 
	 * Given this input string
	 * "An input string, this is! (or is it?)"
	 * 
	 * The output list of strings should be
	 * ["an", "input", "string", "this", "is", "or", "is", "it"]
	 * 
	 * @param input The file to read in and tokenize.
	 * @return The list of tokens (words) from the input file, ordered by occurrence.
	 * @throws IOException 
	 */
	public static ArrayList<String> tokenizeFile(File input) {
		BufferedInputStream buffer;
		try {
			buffer = new BufferedInputStream(new FileInputStream(input));
				Scanner scan = new Scanner(buffer);
		
			ArrayList<String> tokens = new ArrayList<String>(); 
			while( scan.hasNextLine()){
				String line = scan.nextLine();
				for(String w : line.split("\\W")){
					tokens.add(w.toLowerCase());
				}
			}
			return tokens;
		} catch ( IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Takes a list of {@link Frequency}s and prints it to standard out. It also
	 * prints out the total number of items, and the total number of unique items.
	 * 
	 * Example one:
	 * 
	 * Given the input list of word frequencies
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total item count: 6
	 * Unique item count: 5
	 * 
	 * sentence	2
	 * the		1
	 * this		1
	 * repeats	1
	 * word		1
	 * 
	 * 
	 * Example two:
	 * 
	 * Given the input list of 2-gram frequencies
	 * ["you think:2", "how you:1", "know how:1", "think you:1", "you know:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total 2-gram count: 6
	 * Unique 2-gram count: 5
	 * 
	 * you think	2
	 * how you		1
	 * know how		1
	 * think you	1
	 * you know		1
	 * 
	 * @param frequencies A list of frequencies.
	 */
	public static void printFrequencies(List<Frequency> frequencies) {
		if (frequencies == null || frequencies.isEmpty()){
			System.out.println();
			System.out.printf("Total item count: 0\n");
			System.out.printf("Unique item count: 0\n");
			return;
		}
		boolean is2gram = frequencies.get(0).getText().contains(" ");
		
		int sum = 0;
		for( Frequency freqenct : frequencies){
			sum += freqenct.getFrequency();
		}
		
		System.out.println();
		if (is2gram){
			System.out.printf("Total 2-gram count: %d\n", sum);
			System.out.printf("Unique 2-gram count: %d\n", frequencies.size());
		}else{
			System.out.printf("Total item count: %d\n", sum);
			System.out.printf("Unique item count: %d\n", frequencies.size());
		}
		System.out.println();
		for( Frequency freq: frequencies){
			System.out.printf("%s\t%d\n", freq.getText(), freq.getFrequency());
		}
		System.out.println();
	}
}
