package ir.assignments.one.d;

import ir.assignments.one.a.Frequency;
import ir.assignments.one.a.Utilities;
import ir.assignments.one.b.WordFrequencyCounter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PalindromeFrequencyCounter {
	/**
	 * This class should not be instantiated.
	 */
	private PalindromeFrequencyCounter() {
	}

	/**
	 * Takes the input list of words and processes it, returning a list of
	 * {@link Frequency}s.
	 * 
	 * This method expects a list of lowercase alphanumeric strings. If the
	 * input list is null, an empty list is returned.
	 * 
	 * There is one frequency in the output list for every unique palindrome
	 * found in the original list. The frequency of each palindrome is equal to
	 * the number of times that palindrome occurs in the original list.
	 * 
	 * Palindromes can span sequential words in the input list.
	 * 
	 * The returned list is ordered by decreasing size, with tied palindromes
	 * sorted by frequency and further tied palindromes sorted alphabetically.
	 * 
	 * The original list is not modified.
	 * 
	 * Example:
	 * 
	 * Given the input list of strings ["do", "geese", "see", "god", "abba",
	 * "bat", "tab"]
	 * 
	 * The output list of palindromes should be ["do geese see god:1",
	 * "bat tab:1", "abba:1"]
	 * 
	 * @param words
	 *            A list of words.
	 * @return A list of palindrome frequencies, ordered by decreasing
	 *         frequency.
	 */
	private static List<Frequency> computePalindromeFrequencies(
			ArrayList<String> words) {
		if (words == null || words.isEmpty()) {
			return new ArrayList<Frequency>(0);
		}
		
		List<String> list = PalindromDetector.getAllPalindromWords(words);
		ArrayList<Frequency> freqs = WordFrequencyCounter
				.computeDictinary(list);
		Collections.sort(freqs, new Comparator<Frequency>() {

			@Override
			public int compare(Frequency o1, Frequency o2) {
				int icmp = o2.getText().split(" ").length
						- o1.getText().split(" ").length;
				if (icmp == 0) {
					icmp = o2.getFrequency() - o1.getFrequency();
					if (icmp == 0) {
						icmp = o2.getText().length() - o1.getText().length();
						if (icmp == 0){
							icmp = o2.getText().compareTo(o1.getText());
						}
					}
				}
				return icmp;
			}

		});
		return freqs;
	}

	/**
	 * Runs the 2-gram counter. The input should be the path to a text file.
	 * 
	 * @param args
	 *            The first element should contain the path to a text file.
	 */
	public static void main(String[] args) {
		File file = new File(args[0]);
		ArrayList<String> words;
		words = Utilities.tokenizeFile(file);
		String[] test = { "do", "geese", "see", "god", "abba", "bat", "tab"};
		String[] test2 = { "a", "a", "c", "a","a","a","a"};
		// words = new ArrayList<String>();
		for (String str : test) {
			words.add(str);
		}
		for (String str : test2) {
			words.add(str);
		}
		List<Frequency> frequencies = computePalindromeFrequencies(words);
		Utilities.printFrequencies(frequencies);
	}
}
