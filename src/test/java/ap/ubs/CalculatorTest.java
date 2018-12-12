package ap.ubs;

import static org.junit.Assert.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ap.ubs.Calculator;

public class CalculatorTest {
	
	private static SecureRandom random = new SecureRandom();

	@Test
	public void testDefaultDelimiterAndLines() {
		check("", 0);
		check("1,2", 3);
		check("1,2,", 3); //TODO: throw exception if it ends with delimiter ?
		check("1\n2,3", 6);
	}
	
	@Test
	public void testCustomDelimiter() {
		check("Custom delimiter", "//;\n1;2;3\n4", 10);
		check("Multi-char delimiter", "//***\n1***2***3", 6);
		check("Multiple delimiters", "//**|%%\n1**2%%3\n1", 7);
		check("- in delimiter", "//a-b\n1a-b2a-b5", 8);
		check("Number in delimiter", "//a12\n1a122a125", 8);
		check("Number in delimiter, empty", "//a12\n", 0);
//		check("'New line' in delimiter", "//\n\n1a122a125", 8);
		check("Empty delimiter. Only 'new line' is used", "//\n1\n2\n3", 6); 
	}
	
	@Test
	public void testOrder() {
		check("//L|LZ\n434906LZ25990238LZ37369006", 63794150);
	}
	
	@Test
	public void testNegatives() {
		try {
			String input = "//;\n1;-2;3\n-4";
			Calculator.add(input);
			fail("Should fail for negative: " + input);
		} catch (Exception ex) {
			String errorMessage = "negatives not allowed";
			assertTrue(ex.getMessage(), ex.getMessage().startsWith(errorMessage));
			assertTrue(ex.getMessage(), ex.getMessage().contains("-2"));
			assertTrue(ex.getMessage(), ex.getMessage().contains("-4"));
		}
	}
	
	@Test
	public void propertyTest() {
		int delimitersNumber = 20;
		int delimitersMaxLength = 20;
		int valuesNumber = 100000;
		int repeats = 100;
		
		List<String> allDelimiters = new ArrayList<>();
		List<String> generated = new ArrayList<>();
		List<Long> generatedResults = new ArrayList<>();
		for (int r = 0; r < repeats; r++) {
			List<String> delims = generateDelimiters(delimitersNumber, delimitersMaxLength);
			List<Integer> numbers = generateNumbers(valuesNumber);
			StringBuilder result = new StringBuilder("//");
			for (String delim: delims) {
				if (result.length() > 2) {
					result.append("|");
				}
				result.append(delim);
			}
			allDelimiters.add(delims.toString());
			delims.add("\n"); //after delimiters string is generated
			result.append("\n");
			if (numbers.size() > 0) {
				result.append(numbers.get(0));
			}
			for (int i = 1; i < numbers.size(); i++) {
				String delim = delims.get(random.nextInt(delims.size()));
				result.append(delim);
				result.append(numbers.get(i));
			}
			
			String input = result.toString();
			long sum = 0;
			for(int num: numbers) {
				sum += num;
			}
			generated.add(input);
			generatedResults.add(sum);
		}
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < generated.size(); i++) {
			String input = generated.get(i);
			System.out.println("Case " + i + ": length=" + input.length() + " delims=" + allDelimiters.get(i)); // + input);
			assertEquals((long)generatedResults.get(i), (long)Calculator.add(input));
		}
		long duration = System.currentTimeMillis() - start;
		System.out.println("Duration of propery base testing: " + duration + " msec");
		
		
	}

	public static void check(String title, String input, int expected) {
		String message = null;
		if (title != null && title.length() > 0) {
			message = title + ": " + input;
		} else {
			message = input;
		}
		assertEquals(message, expected, Calculator.add(input));
	}
	
	public static void check(String input, int expected) {
		check(null, input, expected);
	}
	
	public static List<String> generateDelimiters(int number, int maxLength) {
//		System.out.println((char)80);
		List<String> result = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			StringBuilder del = new StringBuilder();
			int targetLength = 1 + random.nextInt(maxLength-1);
			while (del.length() < targetLength) {
//				int chInt = random.nextInt(126-32) + 32;
//				int chInt = random.nextInt(Character.MAX_VALUE) + 32;
				int chInt = random.nextInt(300);// + 32;
				char ch = (char) chInt;
				if (ch != '\n' && ch != '|' && !Calculator.isDigitChar(ch)) {
//					System.out.println("Delim: " + chInt + " -> '" + ch + "'");
					del.append(ch);
				}
			}
			result.add(del.toString());
//			System.out.println("--");
		}
		return result;
	}
	
	public static List<Integer> generateNumbers(int number) {
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < number; i++) {
			result.add(Math.abs(random.nextInt(1000000)));
		}
		return result;
	}
	
}
