package ir.assignments.one.d;

import java.util.LinkedList;
import java.util.List;

public class PalindromDetector {
	
	public PalindromDetector() {
		historyString = new LinkedList<StringBuilder>();
	}

	/**
	 * Take a new word and produce new palindroms 
	 * which composed by sequential history words and ended by this word 
	 * @param new word
	 * @return list of new palindroms
	 */
	public List<String> detectBy(String word) {
		this.append(word);
		List<String> parlindroms = new LinkedList<String>();
		for( StringBuilder builder : historyString){
			if (isPalindrom(builder.toString().replaceAll(" ", ""))){
				parlindroms.add(builder.toString());
			}
		}
		return parlindroms;
	}

	/**
	 * append word in to history
	 * @param word
	 */
	public void append(String word) {
		if (word==null || word.isEmpty()){
			return;
		}
		for(StringBuilder builder : historyString){
			builder.append(" ").append(word);
		}
		historyString.add(new StringBuilder(word));
	}
	
	/**
	 * Detect whether input string is a palindrom
	 * only support alphanumeric characters  
	 * @param word
	 * @return
	 */
	public static boolean isPalindrom(String word){
		if (word== null || word.length()<2){
			return false;
		}
		int lstart = (word.length()-1)/2;
		int rstart = lstart;
		if ( word.length() %2 ==0){
			rstart++;
		}
		for( int i = 0; lstart-i >=0; i++ ){
			if ( word.charAt(lstart-i) != word.charAt(rstart+i)){
				return false;
			}
		}
		return true;
	}

	private LinkedList<StringBuilder> historyString;

}
