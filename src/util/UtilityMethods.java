package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

public class UtilityMethods {
	/**
	 * The number of letters that are removed from a word to attempt to find a
	 * category.
	 */
	public static final int SUFFIX_BACKTRACE = 3;
	/** The minimum size of the search string. */
	private static final int MIN_SEARCH_STRING = 3;

	private UtilityMethods() {
		// Do nothing. It cannot be instantiated.
	}

	/**
	 * Splits a string at the given delimiter, but does not split within any
	 * form of brackets.
	 * 
	 * @param string
	 *            The string to split.
	 * @param delimiter
	 *            The delimiter to split by (outside of brackets)
	 * @return An array of strings that do not contain the delimiter except if
	 *         in brackets.
	 */
	public static ArrayList<String> split(String string, char delimiter) {
		ArrayList<String> results = new ArrayList<>();
		boolean quoted = false;
		int bracketCount = 0;
		int prevIndex = 0;
		int i = 0;
		for (i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case '(':
				bracketCount++;
				continue;
			case ')':
				bracketCount--;
				continue;
			case '"':
				quoted = !quoted;
				continue;
			case '\\':
				i++;
				continue;
			}

			if (bracketCount == 0 && !quoted && string.charAt(i) == delimiter) {
				results.add(string.substring(prevIndex, i));
				prevIndex = i + 1;
			}
		}
		if (prevIndex != i)
			results.add(string.substring(prevIndex, i));
		return results;
	}

	private static final Pattern REPL_PATTERN = Pattern.compile("\\$(\\d+)");

	/**
	 * Replaces a $\d encoded token with an indexed string.
	 * 
	 * @param str
	 *            The tokenised string to replace.
	 * @param replacements
	 *            The replacements to use.
	 * @return The tokenised string with replacements instead of tokens.
	 */
	public static String replaceToken(String str, String[] replacements) {
		Matcher m = REPL_PATTERN.matcher(str);
		StringBuffer buffer = new StringBuffer();
		int start = 0;
		while (m.find()) {
			buffer.append(str.substring(start, m.start()));
			buffer.append(replacements[Integer.parseInt(m.group(1))]);
			start = m.end();
		}
		buffer.append(str.substring(start));
		return buffer.toString();
	}

	/**
	 * Creates several possible Wikipedia page titles by manipulating case in
	 * the title and the scope of the title. Also creates possible titles by
	 * removing 'The' from the scope.
	 * 
	 * @param baseTitle
	 *            The base title (Every word capitalised) from which titles are
	 *            created.
	 * @return An array of possible titles.
	 */
	public static Set<String> manipulateStringCapitalisation(String baseTitle) {
		Set<String> possibles = new HashSet<String>();
		if (baseTitle.isEmpty() || baseTitle.equals("("))
			return possibles;

		int index = baseTitle.indexOf("(");
		// If there is a bracket, recurse into the brackets.
		Set<String> brackets = null;
		if (index != -1) {
			try {
				String bracketString = baseTitle.substring(index + 1,
						baseTitle.length() - 1);
				brackets = manipulateStringCapitalisation(bracketString);
				// Check for 'The'
				if (bracketString.length() > 4) {
					if (bracketString.substring(0, 4).equals("The ")) {
						brackets.addAll(manipulateStringCapitalisation(bracketString
								.substring(4)));
					}
				}

				// Set the baseTitle as the stuff before brackets
				if (index > 1)
					baseTitle = baseTitle.substring(0, index - 1);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Title: " + baseTitle);
			}
		}

		// As is
		possibles.add(baseTitle);
		// Capitalised words
		possibles.add(WordUtils.capitalize(baseTitle));
		// Add the lowercase title (tonka truck)
		baseTitle = baseTitle.toLowerCase();
		possibles.add(baseTitle);
		// Add the capitalised first word title (Tonka truck)
		baseTitle = Character.toUpperCase(baseTitle.charAt(0))
				+ baseTitle.substring(1);
		possibles.add(baseTitle);

		// If there were brackets, append them on.
		if (index != -1) {
			Set<String> possiblesComplete = new HashSet<String>();
			for (String basePossible : possibles) {
				for (String bracketPossible : brackets) {
					possiblesComplete.add(basePossible + " (" + bracketPossible
							+ ")");
				}
			}

			possibles = possiblesComplete;
		}
		return possibles;
	}

	/**
	 * Searches for a string within a given target string using a rough
	 * substring search.
	 * 
	 * @param baseString
	 *            The base string to search for.
	 * @param targetString
	 *            The target string in which the search takes place.
	 * @param backtrace
	 *            The number of characters allowed to substring search.
	 * @return True if the string (or some substring thereof) is found.
	 */
	public static boolean findSubString(String baseString, String targetString,
			int backtrace) {
		if (baseString == null || targetString == null)
			return false;
		baseString = baseString.toUpperCase();
		targetString = targetString.toUpperCase();
		// Try different permutations of category searches, removing the
		// last few letters if necessary.
		boolean foundString = false;
		int minSize = Math.min(baseString.length(), MIN_SEARCH_STRING);
		for (int i = 0; i <= backtrace; i++) {
			if (baseString.length() >= minSize + i
					&& targetString.contains(baseString.substring(0,
							baseString.length() - i))) {
				foundString = true;
				break;
			}
		}
		return foundString;
	}

	/**
	 * Searches for a string within a given target string using a rough
	 * substring search.
	 * 
	 * @param baseString
	 *            The base string to search for.
	 * @param targetString
	 *            The target string in which the search takes place.
	 * @return True if the string (or some substring thereof) is found.
	 */
	public static boolean findSubString(String baseString, String targetString) {
		return findSubString(baseString, targetString, SUFFIX_BACKTRACE);
	}

	/**
	 * Simple method for compiling a Matcher object.
	 * 
	 * @param firstSentence
	 *            The sentence being checked.
	 * @param regExpString
	 *            The regular expression for checking.
	 * @return The compiled Matcher from the given arguments.
	 */
	public static Matcher getRegMatcher(String firstSentence,
			String regExpString) {
		Pattern pattern = Pattern.compile(regExpString,
				Pattern.CASE_INSENSITIVE);
		return pattern.matcher(firstSentence);
	}

	/**
	 * Checks if an array contains a given Object (linear search).
	 * 
	 * @param array
	 *            The array.
	 * @param obj
	 *            The Object to search for.
	 * @return True if the array contains the element.
	 */
	public static boolean arrayContains(Object[] array, Object obj) {
		if (array == null || array.length == 0)
			return false;

		for (Object term : array) {
			if (term.equals(obj))
				return true;
		}
		return false;
	}

	/**
	 * Is the string a number?
	 * 
	 * @param string
	 *            The string.
	 * @return True if it represents a number.
	 */
	public static boolean isNumber(String string) {
		return string.matches("[\\d.+-E]+");
	}

	/**
	 * Simple tool for converting long to a string of time.
	 * 
	 * @param time
	 *            The time in millis.
	 * @return A string representing the time.
	 */
	public static String toTimeFormat(long time) {
		String timeString = time / (1000 * 60 * 60) + ":"
				+ (time / (1000 * 60)) % 60 + ":" + (time / 1000) % 60;
		return timeString;
	}

	public static String capitalise(String title) {
		return Character.toUpperCase(title.charAt(0)) + title.substring(1);
	}

	public static void removeNegOnes(Collection<Integer> target) {
		while (target.remove(-1))
			;
	}

	/**
	 * Shrinks a string by a given amount on either side.
	 * 
	 * @param string
	 *            The string to shrink.
	 * @param shrinkage
	 *            The amount to shrink by.
	 * @return The string minus x characters on either side, or the empty string
	 *         if shrinkage exceeds string length.
	 */
	public static String shrinkString(String string, int shrinkage) {
		if (string.length() < shrinkage * 2)
			return "";
		return string.substring(1, string.length() - 1);
	}
}
