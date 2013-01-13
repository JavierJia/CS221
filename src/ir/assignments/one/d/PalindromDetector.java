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
			if (isPalindrom(builder.toString())){
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
		for( int istart = 0, iend = word.length()-1; istart < iend ; istart++, iend-- ){
			while (word.charAt(istart) == ' '){
				istart++;
				if (istart >= iend){
					return true;
				}
			}
			while (word.charAt(iend) == ' '){
				iend--;
				if (istart >= iend){
					return true;
				}
			}
			if ( word.charAt(istart) != word.charAt( iend)){
				return false;
			}
		}
		return true;
	}

	private LinkedList<StringBuilder> historyString;

}
