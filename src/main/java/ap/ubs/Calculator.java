package ap.ubs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class Calculator {

	public static void main(String[] args) {
		System.out.println("1,2 = " + add("1,2"));
		System.out.println("1,2, = " + add("1,2")); //TODO: throw exception ?
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
	//TODO: test with '-' in separator 

	private static int CHAR_0 = (int)'0';
	private static int CHAR_9 = (int)'9';
	private static List<String> DEFAULT_DELIMITERS = new ArrayList<>();
	static {
		DEFAULT_DELIMITERS.add(",");
	}
	
	public static long add(String numbers) {
		long result = 0;
		int index = 0;
		List<String> delims = DEFAULT_DELIMITERS;  //TODO: change to full name
		
		if (numbers.startsWith("//")) {
			int lineEnd = numbers.indexOf('\n');
			delims = parseDelimiters(numbers.substring(2, lineEnd));
			index = lineEnd + 1;
		}
		delims.add("\n");
		String curNumber = "";
		List<Integer> parsedNumbers = new ArrayList<>();
		while (index < numbers.length()) {
			char ch = numbers.charAt(index);
			if (isDigitChar(ch)) {
				curNumber += ch;
				index += 1;
			} else {
				if (curNumber.length() > 0) {
					parsedNumbers.add(Integer.parseInt(curNumber));
					curNumber = "";
				} 
				String nextDelimiter = findDelimiter(numbers, index, delims);
				if (nextDelimiter != null) {
					index += nextDelimiter.length();
				} else {
					throw new IllegalArgumentException("Unknown delimiter: " + numbers.substring(index, Math.min(index+10, numbers.length())));
				}
			}
		}
		if (curNumber.length() > 0) {
			parsedNumbers.add(Integer.parseInt(curNumber));
		} else {
//			throw new IllegalArgumentException("Input should not finish with delimiter");
		}
		
		//TODO: extract negatives check as method
		StringBuilder negatives = new StringBuilder();
		for (int number: parsedNumbers) {
			if (number < 0) {
				if (negatives.length() > 0) {
					negatives.append(", ");
				}
				negatives.append(number);
			}
		}
		if (negatives.length() > 0) {
			throw new IllegalArgumentException("negatives not allowed: " + negatives);
		}
		
//		result = parsedNumbers.stream().reduce(0, Integer::sum);
		for(int num: parsedNumbers) {
			result += num;
		}
		
		return result;
	}
	
	public static boolean isDigitChar(char ch) {
		int chInt = (int)ch; 
		return (chInt >= CHAR_0 && chInt <= CHAR_9) || ch == '-';
	}
	
	public static String findDelimiter(String input, int index, List<String> delimiters) {
		for(String delim: delimiters) {
			if (contains(input, index, delim)) {
				return delim;
			}
		}
		return null;
	}
	
	public static boolean contains(String input, int index, String piece) {
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
	
	public static List<String> parseDelimiters(String input) {
		StringTokenizer tok = new StringTokenizer(input, "|");
		List<String> result = new ArrayList<>();
		while (tok.hasMoreTokens()) {
			result.add(tok.nextToken());
		}
//		Collections.sort(result);
		result.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.length() - o1.length();
			}
		});
		return result;
	}
}
