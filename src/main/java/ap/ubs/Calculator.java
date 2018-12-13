package ap.ubs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

/**
 * @author Alex Prakharenka
 * Dec 11, 2018
 *
 */
public class Calculator {

	private static int CHAR_0 = (int)'0';
	private static int CHAR_9 = (int)'9';
	private static List<String> DEFAULT_DELIMITERS = new ArrayList<>();
	static {
		DEFAULT_DELIMITERS.add(",");
	}
	
	
	public static void main(String[] args) {
		System.out.println("1,2 = " + add("1,2"));
		System.out.println("1,2, = " + add("1,2,")); //we don't fail on this invalid input
		System.out.println("1\\n2,3 = " + add("1\n2,3"));
		System.out.println("//;\\n1;2;3\\n4 = " + add("//;\n1;2;3\n4"));
		try {
			System.out.println("//;\\n1;-2;3\\-n4 = " + add("//;\n1;-2;3\n-4"));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println("//***\\n1***2***3 = " + add("//***\n1***2***3"));
		System.out.println("//*|%\\n1*2%3 = " + add("//*|%\n1*2%3"));
		System.out.println("//**|%%\\n1**2%%3 = " + add("//**|%%\n1**2%%3"));
		
	}
	
	
	/**
	 * Public API function. See details in the assignment.
	 * @param numbers string of numbers
	 * @return sum of numbers
	 */
	public static int add(String numbers) {
		return (int) addL(numbers);
	}
	
	/**
	 * For big sequences sum often overflows int variable. Long is used as a result type.
	 */
	protected static long addL(String numbers) {
		
		int index = 0;
		List<String> delims = DEFAULT_DELIMITERS;  //TODO: change to full name
		
		if (numbers.startsWith("//")) {
			int lineEnd = numbers.indexOf('\n');
			delims = parseDelimiters(numbers.substring(2, lineEnd));
			index = lineEnd + 1;
		}
		delims.add("\n");
		StringBuilder curNumber = new StringBuilder();
		List<Integer> parsedNumbers = new ArrayList<>();
		while (index < numbers.length()) {
			char ch = numbers.charAt(index);
			if (isDigitChar(ch)) {
				curNumber.append(ch);
				index += 1;
			} else {
				if (curNumber.length() > 0) {
					parsedNumbers.add(Integer.parseInt(curNumber.toString()));
					curNumber.setLength(0);
				} 
				Optional<String> nextDelimiter = findDelimiter(numbers, index, delims);
				if (nextDelimiter.isPresent()) {
					index += nextDelimiter.get().length();
				} else {
					throw new IllegalArgumentException("Unknown delimiter: " + numbers.substring(index, Math.min(index+10, numbers.length())));
				}
			}
		}
		if (curNumber.length() > 0) {
			parsedNumbers.add(Integer.parseInt(curNumber.toString()));
		} else {
			//We don't check if input is incorrect, e.g. delimiter at the end.
//			throw new IllegalArgumentException("Input should not finish with delimiter");
		}
		
		return sumAndCheck(parsedNumbers);
	}
	
	/**
	 * Summarize only numbers <= 1000. 
	 * Throws IllegalArgumentException if encounters negative number
	 */
	private static long sumAndCheck(List<Integer> parsedNumbers) {
		StringBuilder negatives = new StringBuilder();
		long result = 0;
		for (int number: parsedNumbers) {
			if (number < 0) {
				if (negatives.length() > 0) {
					negatives.append(", ");
				}
				negatives.append(number);
			}
			if (number > -1 && number < 1001) {
				result += number;
			}
		}
		if (negatives.length() > 0) {
			throw new IllegalArgumentException("negatives not allowed: " + negatives);
		}
		return result;
	}
	
	public static boolean isDigitChar(char ch) {
		int chInt = (int)ch; 
		return (chInt >= CHAR_0 && chInt <= CHAR_9) || ch == '-';
	}
	
	protected static Optional<String> findDelimiter(String input, int index, List<String> delimiters) {
		for(String delim: delimiters) {
			if (containsPiece(input, index, delim)) {
				return Optional.of(delim);
			}
		}
		return Optional.empty();
	}
	
	protected static boolean containsPiece(String input, int index, String piece) {
		for (int i = 0; i < piece.length(); i++) {
			int inputInd = i + index;
			if (inputInd >= input.length()) {
				return false;
			}
			if (input.charAt(inputInd) != piece.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	
	protected static List<String> parseDelimiters(String input) {
		StringTokenizer tok = new StringTokenizer(input, "|");
		List<String> result = new ArrayList<>();
		while (tok.hasMoreTokens()) {
			result.add(tok.nextToken());
		}
		result.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.length() - o1.length();
			}
		});
		return result;
	}
}
