package ir.assignments.one.d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * The linear time code is construct from
 * the thought of 
 * http://www.akalin.cx/longest-palindrome-linear-time
 *
 */
public class PalindromDetector {
	private static String string;
	private static ArrayList<Boolean> isStart;
	private static int UnKnown;

	public static List<String> getAllPalindromWords(ArrayList<String> words) {
		int [] palindromLength = initialized( words);
		constructPalindromLength(0, palindromLength);
		return recoverWordfromPalindrom( palindromLength);
	}
	
	/**
	 * initialize the concat {@link string} / {@link isStart} 
	 * @param words : Input wordlist
	 * @return the initialized palindrom length array
	 */
	private static int [] initialized(ArrayList<String> words) {
		StringBuilder builder = new StringBuilder();
		isStart = new ArrayList<Boolean>();
		// concatenate words
		for ( String word : words){
			if (word.length() < 1){
				continue;
			}
			builder.append(word);
			isStart.add(true);
			for(int i = 1; i < word.length(); i++ ){
				isStart.add(false);
			}
		}
		string = builder.toString();
		// initialize the length at each position, 2N+1 total,
		// don't require the one before the first and the one after the last 
		int [] palindromLength = new int [string.length()*2-1];
		UnKnown = - string.length();
		for ( int i = 0 ; i < palindromLength.length; i++ ){
			palindromLength[i] = UnKnown;
		}
		return palindromLength;
	}

	/**
	 * Construct Palindrome Length Array
	 * This array record the longest palindrome centered as current position 
	 * @param start
	 * @param palindromLength
	 */
	private static void constructPalindromLength(int start, int[] palindromLength) {
		if (palindromLength[start] == UnKnown){
			palindromLength[start] = expendFromCenter(start, 1);
		}else{
			palindromLength[start] = expendFromCenter(start, -palindromLength[start]);
		}
		int next = fillUpFuthurByHistory(start, palindromLength);
		if ( next >= palindromLength.length){
			return;
		}
		constructPalindromLength( next, palindromLength);
	}
	
	/**
	 * fillup the right half array centered from start;
	 * e.g.
	 * 	 	a   a   c   a   a
	 * pos: 0 1 2 3 4 5 6 7 8
	 * val[ 1 2 1 0 5 ? ? ? ? ]
	 * 
	 * the value of palindromLength is the length of palindrom centered by current position
	 * without compare, we can directly construct the unknown value from left side
	 * val[ 1 2 1 0 5 0 1 2? ?]
	 * 
	 * the ? means that the palindrom from this postion is at least 2;
	 * in code, we represent ? as negative number.
	 * @param start
	 * @param palindromLength
	 * @return index of next unknown number
	 */
	private static int fillUpFuthurByHistory(int start, int[] palindromLength) {
		for ( int i = 1; start - i >= 0
				&& start + i < palindromLength.length
				&& i < palindromLength[start]-1; i++ ){
			// if palindromLength[start -i] inside palindromLength[ start]
			if ( palindromLength[start-i] < 2){
				palindromLength[start+i] = palindromLength[start-i];
				continue;
			}
			if ( palindromLength[start-i] + i <= palindromLength[start]){
				palindromLength[start+i]= -palindromLength[start-i];
			}
		}
		int firstUnknow = start+1;
		while( firstUnknow < palindromLength.length && palindromLength[firstUnknow]>=0){
			firstUnknow++;
		}
		return firstUnknow;
	}

	/**
	 * expend left and right to decide the longest palindrome
	 * @param start : centered position
	 * @param minimal: the minimal skipped compare number
	 * @return
	 */
	private static int expendFromCenter(int start, int minimal) {
		if ( minimal < 1){
			System.err.println("skip length < 1 !");
			return 0;
		}

		int left;
		int right;
		if ( start % 2 != 0){
			// odd number, center is interval. minimal must by even
			if ( minimal == 1){
				minimal = 0;
			}
			left = (start-1) /2 - minimal/2;
			right = (start+1) /2 + minimal/2;
		}else{
			// even number, center is letter. minimal must by odd.
			left = start /2 - (minimal+1)/2;
			right = start /2 + (minimal+1)/2;
		}
		int length = minimal;
		while (left >= 0 && right < string.length() &&
				string.charAt(left) == string.charAt(right) ){
			left--;
			right++;
			length+=2;
		}
		return length;
	}

	/**
	 * reconstruct word segment from isStart array
	 * @param palindromLength
	 */
	private static List<String> recoverWordfromPalindrom(
			int[] palindromLength) {
		LinkedList<String> list = new LinkedList<String>();
		for(int center =0; center < palindromLength.length; center+=2){
			for(int len = 1; len < (palindromLength[center]+1)/2; len++){
				int start = center/2 - len;
				int end = center/2 + len;
				if ( IsStart(start) && IsEnd(end)){
					list.add(FormatWord(start,end));
				}
			}
		}
		for(int center =1; center< palindromLength.length; center+=2){
			for(int len = 0; len < (palindromLength[center]/2) ;len++){
				int start = (center-1)/2 - len;
				int end = (center+1)/2 + len;
				if ( IsStart(start) && IsEnd(end)){
					list.add(FormatWord(start,end));
				}
			}
		}
		
		// add the word of length 1 into list;
		for(int i = 0; i < string.length(); i++){
			if( IsStart(i) && IsEnd(i)){
				list.add(String.valueOf(string.charAt(i)));
			}
		}
		return list;
	}

	private static String FormatWord(int start, int end) {
		StringBuilder builder = new StringBuilder();
		int i = start;
		builder.append(string.charAt(i));
		for( i= start +1 ; i <= end; i++){
			if ( IsStart(i)){
				builder.append(" ");
			}
			builder.append(string.charAt(i));
		}
		return builder.toString();
	}

	private static boolean IsEnd(int end) {
		return end == string.length()-1 || isStart.get(end+1)== true;
	}

	private static boolean IsStart(int start) {
		return isStart.get(start);
	}

	/**
	 * Detect whether input string is a palindrom only support alphanumeric
	 * characters
	 * 
	 * @param word
	 * @return
	 */
	public static boolean isPalindrom(String word) {
		if (word == null || word.length() < 2) {
			return false;
		}
		for (int istart = 0, iend = word.length() - 1; istart < iend; istart++, iend--) {
			while (word.charAt(istart) == ' ') {
				istart++;
				if (istart >= iend) {
					return true;
				}
			}
			while (word.charAt(iend) == ' ') {
				iend--;
				if (istart >= iend) {
					return true;
				}
			}
			if (word.charAt(istart) != word.charAt(iend)) {
				return false;
			}
		}
		return true;
	}



}
