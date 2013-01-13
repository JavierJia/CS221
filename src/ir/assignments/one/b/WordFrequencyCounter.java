package ir.assignments.one.b;

import ir.assignments.one.a.Frequency;
import ir.assignments.one.a.Utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Counts the total number of words and their frequencies in a text file.
 */
public final class WordFrequencyCounter {
	/**
	 * This class should not be instantiated.
	 */
	private WordFrequencyCounter() {}
	
	/**
	 * Takes the input list of words and processes it, returning a list
	 * of {@link Frequency}s.
	 * 
	 * This method expects a list of lowercase alphanumeric strings.
	 * If the input list is null, an empty list is returned.
	 * 
	 * There is one frequency in the output list for every 
	 * unique word in the original list. The frequency of each word
	 * is equal to the number of times that word occurs in the original list. 
	 * 
	 * The returned list is ordered by decreasing frequency, with tied words sorted
	 * alphabetically.
	 * 
	 * The original list is not modified.
	 * 
	 * Example:
	 * 
	 * Given the input list of strings 
	 * ["this", "sentence", "repeats", "the", "word", "sentence"]
	 * 
	 * The output list of frequencies should be 
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 *  
	 * @param words A list of words.
	 * @return A list of word frequencies, ordered by decreasing frequency.
	 */
	public static List<Frequency> computeWordFrequencies(List<String> words) {
		ArrayList<Frequency> list = computeDictinary(words);
		Collections.sort(list, new Comparator<Frequency>(){
			@Override
			public int compare(Frequency o1, Frequency o2) {
				//reversed sequence
				return o2.getFrequency() - o1.getFrequency();
			}
		});
		return list;
	}
	
	public static ArrayList<Frequency> computeDictinary(List<String> words){
		Map<String,Integer> dict = new HashMap<String,Integer>(words.size()<<1); 
		for( String word: words){
			if ( dict.containsKey(word)){
				dict.put(word, dict.get(word)+1);
			}else{
				dict.put(word, 1);
			}
		}
		ArrayList<Frequency> list = new ArrayList<Frequency>(dict.size());
		for( Map.Entry<String, Integer> entry: dict.entrySet()){
			list.add(new Frequency(entry.getKey(), entry.getValue()));
		}
		return list;
	}
	
	/**
	 * Runs the word frequency counter. The input should be the path to a text file.
	 * 
	 * @param args The first element should contain the path to a text file.
	 */
	public static void main(String[] args) {
		File file = new File(args[0]);
		List<String> words = Utilities.tokenizeFile(file);
		List<Frequency> frequencies = computeWordFrequencies(words);
		Utilities.printFrequencies(frequencies);
	}
}
