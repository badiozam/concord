/*
 * MIT License
 * 
 * Copyright (c) 2018 i4one Interactive, LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.i4one.base.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.web.servlet.support.RequestContext;

/**
 *  Provides a set of utility functions
 *
 * @author Hamid Badiozamani
 */
public class Utils
{
	/**
	 * The URL starting identifier
	 */
	private static final String URL_BEGIN = "[%url=";
	/**
	 * The maximum number of bytes to store in memory when uploading (2MB)
	 */
	public static final int MAX_MEMORY_FILESIZE = 2 * 1000 * 1024;
	/**
	 * The maximum number of bytes to store on the file system when uploading files  (20MB)
	 */
	public static final int MAX_DISK_FILESIZE = 20 * 1000 * 1024;
	/**
	 * The temporary directory where files will be stored
	 */
	public static final String UPLOAD_REPOSITORY = "/tmp";
	/**
	 * Number of seconds in a year
	 */
	public static final int SECONDS_PER_YEAR = 365 * 24 * 60 * 60;

	/**
	 * UUID for use as an identifier for this JVM instance
	 */
	public static final String UUID_STRING = UUID.randomUUID().toString();

	private static final Pattern INTEGER_PATTERN  = Pattern.compile("-?\\d+");

	/**
	 * Returns a the given parameter or an empty string if the parameter given is null
	 *
	 * @param str The string to evaluate
	 *
	 * @return The empty string if the string is null, or the
	 *	string itself otherwise
	 */
	public static String forceEmptyStr(String str)
	{
		return defaultIfNull(str, "");
	}

	/**
	 * Tests an object method's return value against the same method on another
	 * object. Returns true if the return values are null, false if one is null
	 * but not the other and the value of .equals(..) on the return value if
	 * neither are null
	 *
	 * @param <U> The type of the input objects
	 * @param <T> The type of object that the method "function" returns
	 *
	 * @param obj1 The first object
	 * @param obj2 The second object
	 * 
	 * @param function The method to call on each object to get the comparison object
	 * 
	 * @return Whether the return values of the function are equal or not
	 */
	public static <U, T> boolean equalsOrNull(U obj1, U obj2, Function<U, T> function)
	{
		T left = function.apply(obj1);
		T right = function.apply(obj1);

		return Objects.equals(left, right);
	}

	/**
	 * Returns a the given parameter or a default string if the parameter given is null
	 *
	 * @param <U> The type of object to check
	 * @param obj The object to evaluate
	 * @param defObj The object to return if the argument is null
	 *
	 * @return The default object if the object is null, or the
	 *	string itself otherwise
	 */
	public static <U extends Object> U defaultIfNull(U obj, U defObj)
	{
		return ( obj == null ) ? defObj : obj;
	}

	/**
	 * Returns a the given parameter or a default string if the parameter given is empty
	 *
	 * @param str The string to evaluate
	 * @param defStr The string to return if the argument is null
	 *
	 * @return The default string if the string is empty, or the
	 *	string itself otherwise
	 */
	public static String defaultIfEmpty(String str, String defStr)
	{
		return isEmpty(str) ? defStr : str;
	}

	/**
	 * Returns a default number if the string given is not parseable
	 *
	 * @param str The string to parse
	 * @param def The default integer to return if the string is not parseable
	 *
	 * @return The resulting parsed or default number
	 */
	public static int defaultIfNaN(String str, int def)
	{
		try
		{
			// Empty and null strings are not numbers
			//
			if ( isEmpty(str))
			{
				return def;
			}
			else
			{
				return Integer.parseInt(str);
			}
		}
		catch ( NumberFormatException nfe )
		{
			return def;
		}
	}

	/**
	 * Returns a default number if the string given is not parseable
	 *
	 * @param str The string to parse
	 * @param def The default long integer to return if the string is not parseable
	 *
	 * @return The resulting parsed or default number
	 */
	public static long defaultIfNaL(String str, long def)
	{
		try
		{
			// Empty and null strings are not numbers
			//
			if ( isEmpty(str))
			{
				return def;
			}
			else
			{
				return Long.parseLong(str);
			}
		}
		catch ( NumberFormatException nfe )
		{
			return def;
		}
	}

	/**
	 * Returns a default float if the string given is not parseable
	 *
	 * @param str The string to parse
	 * @param def The default float to return if the string is not parseable
	 *
	 * @return The resulting parsed or default number
	 */
	public static float defaultIfNaF(String str, float def)
	{
		try
		{
			// Empty and null strings are not numbers
			//
			if ( isEmpty(str))
			{
				return def;
			}
			else
			{
				return Float.parseFloat(str);
			}
		}
		catch ( NumberFormatException nfe )
		{
			return def;
		}
	}

	/**
	 * Returns a default double if the string given is not parseable
	 *
	 * @param str The string to parse
	 * @param def The default double to return if the string is not parseable
	 *
	 * @return The resulting parsed or default number
	 */
	public static double defaultIfNaD(String str, double def)
	{
		try
		{
			// Empty and null strings are not numbers
			//
			if ( isEmpty(str))
			{
				return def;
			}
			else
			{
				return Double.parseDouble(str);
			}
		}
		catch ( NumberFormatException nfe )
		{
			return def;
		}
	}

	/**
	 * Returns a default boolean if the string given is not a boolean
	 *
	 * @param str The string to parse
	 * @param def The default boolean to return if the string is not parseable
	 *
	 * @return The resulting parsed or default boolean
	 */
	public static boolean defaultIfNaB(String str, boolean def)
	{
		return ( str == null ) ? def : getBoolean(str);
	}

	/**
	 * Tests to see if the given parameter is empty or not
	 *
	 * @param str The string to test
	 *
	 * @return true if the string is null or empty, false otherwise
	 */
	public static boolean isEmpty(String str)
	{
		return ( str == null ) || ( str.trim().length() == 0 );
	}

	/**
	 * Tests to see if the given character is a digit or not
	 *
	 * @param c The character to test
	 *
	 * @return true if the character is a digit, false otherwise
	 */
	public static boolean isDigit(char c)
	{
		return c >= '0' && c <= '9';
	}

	/**
	 * Escapes a string to encode HTML brackets
	 *
	 * @param str The HTML string to escape
	 *
	 * @return The string with escaped brackets
	 */
	public static String htmlEscape(String str)
	{
		return str.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	/**
	 * Escapes a string to escape double-quotes
	 *
	 * @param str The string to escape
	 *
	 * @return The string with escaped double-quotes
	 */
	public static String quotesEscape(String str)
	{
		return str.replaceAll("\"", "\\\"");
	}

	/**
	 * Escapes a string to escape double-quotes CSV style
	 *
	 * @param str The string to escape
	 *
	 * @return The string with CSV escaped double-quotes
	 */
	public static String csvEscape(String str)
	{
		return str.replaceAll("\"", "\"\"");
	}

	/**
	 * Removes all characters that are invalid under CDATA.
	 * Valid CDATA charactesr according to the spec are:
	 * #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
	 *
	 * @param str The string to escape
	 *
	 * @return The string with all non CDATA characters removed
	 */
	public static String stripNonCDATA(String str)
	{
		if ( str != null )
		{
			// Need to go character by character
			//
			int strLen = str.length();
			StringBuilder retVal = new StringBuilder(strLen);

			for ( int i = 0; i < strLen; i++ )
			{
				char c = str.charAt(i);

				// Make sure the character is valid
				//
				if (( c == 0x9 ) ||
				        ( c == 0xd ) ||
				        ( c == 0xa ) ||
				        ( c >= 0x20 && c <= 0xd7ff ) ||
				        ( c >= 0xe000 && c <= 0xfffd ) ||
				    ( c >= 0x10000 && c <= 0x10FFFF ))
				{
					retVal.append(c);
				}
			}

			return retVal.toString();
		}
		else
		{
			return null;
		}
	}

	/**
	 *  Escapes a string to encode SQL single-quotes
	 *
	 * @param str The SQL string to escape
	 *
	 * @return The string with escaped single-quotes
	 */
	public static String sqlEscape(String str)
	{
		return str.replaceAll("'", "''");
	}

	/**
	 * Escapes a string to encode JavaScript 's
	 *
	 * @param str The javascript string to escape
	 *
	 * @return The string with escaped 's
	 */
	public static String jsEscape(String str)
	{
		return forceEmptyStr(str).replaceAll("'", "\\\\'").replaceAll("\r", "").replaceAll("\n", "\\\\\n");
	}

	/**
	 * Breaks a string containing whitespace into HTML breaks at the first
	 * available position after the given breakpoint.
	 * 
	 * @param str The string to break
	 * @param breakPoint The minimum number of characters on each line
	 * 
	 * @return An HTML string with &lt;br&gt; tags for breaking up larger
	 * 	strings.
	 */
	public static String breakToHTML(String str, int breakPoint)
	{
		// Break up the answer and insert HTML breaks so that long answers
		// dont' shrink the chart down by taking up all of the horizontal
		// space
		//
		String trimmed = Utils.trimString(str);
		if ( trimmed.length() <= breakPoint )
		{
			return trimmed;
		}
		else
		{
			int calcBreakPoint = breakPoint;
			for ( ; calcBreakPoint < trimmed.length(); calcBreakPoint++ )
			{
				// Walk until we have whitespace
				//
				char c = trimmed.charAt(calcBreakPoint);
				if ( c == ' ' || c == '\t' || c == '\n' )
				{
					break;
				}
			}

			if ( trimmed.length() == calcBreakPoint )
			{
				return trimmed;
			}
			else
			{
				return trimmed.substring(0, calcBreakPoint) + "<br>" + breakToHTML(trimmed.substring(calcBreakPoint), breakPoint);
			}
		}
	}

	/**
	 * Tests to see if a string is in all upper-case
	 *
	 * @param str The string to test
	 *
	 * @return true if it is all uppercase, false otherwise
	 */
	public static boolean isAllUpperCase(String str)
	{
		// First convert the string to characters
		//
		char[] strChars = str.toCharArray();

		// Go through each character and the first
		// char that is not upper-case, break out
		// and return false
		//
		for ( int i = 0; i < strChars.length; i++ )
		{
			// If it's lower-case we're done, any other character
			// we'll continue
			//
			if ( strChars[i] >= 'a' && strChars[i] <= 'z' )
			{
				return false;
			}
		}

		// If we got this far it means that the string
		// was in all caps
		//
		return true;
	}

	/**
	 * Tests to see if a string is in all lower-case
	 *
	 * @param str The string to test
	 *
	 * @return true if it is all lower, false otherwise
	 */
	public static boolean isAllLowerCase(String str)
	{
		// First convert the string to characters
		//
		char[] strChars = str.toCharArray();

		// Go through each character and the first
		// char that is not lower-case, break out
		// and return false
		//
		for ( int i = 0; i < strChars.length; i++ )
		{
			// If it's upper-case we're done, any other character
			// we'll continue
			//
			if ( strChars[i] >= 'A' && strChars[i] <= 'Z' )
			{
				return false;
			}
		}

		// If we got this far it means that the string
		// was in all lower case
		//
		return true;
	}

	/**
	 * Converts a string to proper naming by moving "The" to the
	 * end of the string and capitalizing the first letter
	 *
	 * @param val The value to make proper
	 *
	 * @return The proper version of the input value
	 */
	public static String toProperName(String val)
	{
		if ( val == null )
		{
			return null;
			}
		else
			{
			String retVal;

			// Ensure the name doesn't start with "the", "a" or "An"
			//
			if ( val.toLowerCase().startsWith("the "))
			{
				retVal = val.replaceFirst("[Tt][Hh][Ee] ", "");
				retVal += ", The";
			}
			else if ( val.toLowerCase().startsWith("a "))
			{
				retVal = val.replaceFirst("[Aa] ", "");
				retVal += ", A";
			}
			else if ( val.toLowerCase().startsWith("an "))
			{
				retVal = val.replaceFirst("[Aa][Nn] ", "");
				retVal += ", An";
			}
			else
			{
				retVal = val;
			}

			if ( isEmpty(retVal) || retVal.length() < 1 )
			{
				return retVal;
			}
			else
			{
				retVal = retVal.substring(0, 1).toUpperCase() + retVal.substring(1);
				return retVal;
			}
		}
	}

	/**
	 * Returns a stringified version of a number with at least
	 * the number of characters specified.
	 *
	 * @param i The integer to convert to string
	 * @param minChars The minimum number of characters to return
	 *
	 * @return The stringified version of the number padded with zeros to meet the min char requirement
	 */
	public static String padZeros(long i, int minChars)
	{
		// Create a new string buffer with at least the minimum number
		// of chars required
		//
		StringBuilder retVal = new StringBuilder(minChars);

		// Now see how long the integer will be
		//
		String intVal = String.valueOf(i);

		// Calculate how much we need to pad
		//
		int numPads = minChars - intVal.length();

		// Pad the string based on how many chars we're
		// deficient
		//
		for ( int j = 0; j < numPads; j++ )
		{
			retVal.append("0");
		}

		// Append the integer value at the end
		//
		retVal.append(intVal);

		// Return the final result
		//
		return retVal.toString();
	}

	/**
	 *  Provides an integer based power function
	 *
	 * @param base The base integer to raise
	 * @param exp The exponent to raise it to
	 *
	 * @return The base raised to the exponent
	 */
	public static long power(int base, int exp)
	{
		// Anything raised to the zero is 1
		//
		if ( exp == 0 )
		{
			return 1;
		}
		else if ( exp == 1 )
		{
			// Anything to the power of 1 is itself
			//
			return base;
		}
		else
		{
			// y^x = y * y^(x-1)
			//
			return base * power(base, exp - 1);
		}
	}

	/**
	 * Returns the current time in terms of seconds since the epoch GMT
	 *
	 * @return The UNIX time in seconds
	 */
	public static int currentTimeSeconds()
	{
		// This function mainly exists to ensure the casting
		// is done properly
		//
		return (int)( System.currentTimeMillis() / 1000 );
	}

	/**
	 * Returns the current time as a Date object
	 *
	 * @return The current time as a Date object
	 */
	public static Date currentDateTime()
	{
		Date retVal = new Date();
		retVal.setTime(System.currentTimeMillis());

		return retVal;
	}

	/**
	 * Verifies that the current time falls between the start time and end time.
	 *
	 * @param starttm The start time
	 * @param endtm The end time
	 *
	 * @return true if the current time falls between the start and end times
	 */
	public static boolean verifyTime(int starttm, int endtm)
	{
		return verifyTime(starttm, endtm, currentTimeSeconds());
	}
	/**
	 * Verifies that the time given falls between the start time and end time.
	 *
	 * @param starttm The start time
	 * @param endtm The end time
	 * @param currTime The current time
	 *
	 * @return true if the time falls between the start and end times
	 */
	public static boolean verifyTime(int starttm, int endtm, int currTime)
	{
		return starttm <= currTime && currTime <= endtm;
	}

	/**
	 * Determines whether the argument constitutes a true or false value
	 *
	 * @param arg The argument to consider
	 *
	 * @return True if the argument is either "1", "t", or "true"
	 */
	public static boolean getBoolean(String arg)
	{
		return ( arg == null ) ? false : ( arg.equalsIgnoreCase("true") || arg.equals("1") || arg.equalsIgnoreCase("t"));
	}

	/**
	 *  Removes all leading and tail whitespace
	 *
	 * @param str The string to trim
	 *
	 * @return The trimmed string, or null if the pointer passed is null
	 */
	public static String trimString(String str)
	{
		if ( str == null )
		{
			return null;
		}
		else
		{
			return str.trim();
		}
	}

	/**
	 *  Abbreviates a string by ensuring that it doesn't go beyond a particular size.
	 * If it doesn't, the string itself is returned. Otherwise, replaces the last
	 * three chars with "..."
	 *
	 * @param str The string to be abbreviated
	 * @param max The maximum number of characters the string is allowed to have
	 *
	 * @return A string not exceeding max chars
	 */
	public static String abbrStr(String str, int max)
	{
		// Check to see if we need to do anything
		//
		if ( str == null || str.length() <= max )
		{
			// Return null if we're given null and
			// if we're given a string that's less
			// than the max, return it untouched.
			//
			return str;
		}
		else
		{
			// Get all the characters except the last 3
			//
			return new StringBuilder(str.substring(0, max - 3)).append("...").toString();
		}
	}

	/**
	 * Performs substitutions of variables in a hash
	 *
	 * @param attrs The attributes that are to be replaced in name/value pair format
	 * @param content The content that is to be analyzed
	 *
	 * @return A final string with all variables replaced
	 */
	public static String subVars(Map<String, Object> attrs, String content)
	{
		return subVars(attrs, "[%", "/", "%]", content);
	}

	public static Set<String> getVars(String content)
	{
		return getVars("[%", "/", "%]", content);
	}

	/**
	 * This method performs substitutions of variables in a hash
	 *
	 * @param attrs The attributes that are to be replaced in name/value
	 *              pair format
	 * @param startTag A string signifying the start of a tag
	 * @param compTag A string appended to the start tag that signifies a complement tag
	 * @param endTag A string signifying the end of a tag
	 * @param content The content that is to be analyzed
	 *
	 * @return A final string with all variables replaced
	 */
	public static String subVars(Map<String, Object> attrs, String startTag, String compTag, String endTag, String content)
	{
		if ( content != null )
		{
			// Create a string buffer that is at least the size
			// of the content
			//
			StringBuilder buff = new StringBuilder(content.length());

			// Go through  the string and find indexes of the start character
			//
			int startIndex = 0;
			int endIndex = 0;

			int startTagLength = startTag.length();
			int endTagLength = endTag.length();

			// Continue until we have no more end tags
			//
			boolean done = false;
			while ( !done )
			{
				// Look for our start index
				//
				startIndex = content.indexOf(startTag, endIndex);

				// System.out.println("Found a start index of " + startIndex);

				// If we found it, look for the end index
				//
				done = ( startIndex < 0 );
				if ( !done )
				{
					// System.out.println("Appending index " + endIndex + " thru " + startIndex);

					// From the last termination character up until this starting character
					// append the contents to our return value
					//
					buff.append( content.substring(endIndex, startIndex) );

					// This is the termination index
					//
					endIndex = content.indexOf(endTag, startIndex) + endTagLength;

					// System.out.println("Found an end index of " + endIndex);

					// if there is no end tag, append the rest of the content
					//
					if ( endIndex == 1 )
					{
						buff.append(content.substring(startIndex, content.length()) );
						continue;
					}

					// figure out what start index to use in case there's a situation like this:
					// [%....[%...%]
					//
					startIndex = getStartIndex(buff, startTag, content, startIndex, endIndex);

					// Get the tag in between
					//
					String tag = content.substring(startIndex + startTagLength, endIndex - endTagLength);

					// System.out.println("Found tag by the name of " + tag);

					// This is the value of the tag from the map
					//
					Object value = attrs.get(tag);

					// System.out.println("Tag's value is " + value);

					// See if we have a value for this tag
					//
					if ( value != null )
					{
						// See if the value was a boolean object type
						//
						if ( value instanceof Boolean )
						{
							// This is the compliment tag
							//
							String currCompTag = startTag + compTag + tag + endTag;

							// See what the value is
							//
							Boolean boolVal = (Boolean)value;
							if ( !boolVal )
							{
								// Skip to the point where we find the complement tag
								//
								int compTagStart = content.indexOf(currCompTag, endIndex);

								// If we don't find the complement tag, then treat
								// the value as a string
								//
								if ( compTagStart < 0 )
								{
									// Append the boolean value
									//
									buff.append(value.toString());
								}
								else
								{
									endIndex = compTagStart + currCompTag.length();
								}
							}
						}
						else
						{
							buff.append(value.toString());
						}
					}
					else
					// We don't have a value for this tag, if it's not an ending tag
					// leave the tag value inside the message
					//
					if ( !tag.startsWith(compTag) )
					{
						buff.append(startTag);
						buff.append(tag);
						buff.append(endTag);
					}
				}
			}

			if ( endIndex != 1 )
			{
				// Append the rest of the string
				//
				buff.append( content.substring(endIndex, content.length()) );
			}

			// Finally return the cooked string
			//
			return buff.toString();
		}
		else
		{
			return null;
		}
	}

	/**
	 * This method finds and returns a list of all tags present in a given string
	 *
	 * @param startTag A string signifying the start of a tag
	 * @param compTag A string appended to the start tag that signifies a complement tag
	 * @param endTag A string signifying the end of a tag
	 * @param content The content that is to be analyzed
	 *
	 * @return A list of all tags that are present in a given string
	 */
	public static Set<String> getVars(String startTag, String compTag, String endTag, String content)
	{
		LinkedHashSet<String> retVal = new LinkedHashSet<>();

		if ( content != null )
		{
			// Go through  the string and find indexes of the start character
			//
			int startIndex = 0;
			int endIndex = 0;

			int startTagLength = startTag.length();
			int endTagLength = endTag.length();

			// Continue until we have no more end tags
			//
			boolean done = false;
			while ( !done )
			{
				// Look for our start index
				//
				startIndex = content.indexOf(startTag, endIndex);

				// System.out.println("Found a start index of " + startIndex);

				// If we found it, look for the end index
				//
				done = ( startIndex < 0 );
				if ( !done )
				{
					// This is the termination index
					//
					endIndex = content.indexOf(endTag, startIndex) + endTagLength;

					// if there is no end tag, append the rest of the content
					//
					if ( endIndex == 1 )
					{
						continue;
					}

					// figure out what start index to use in case there's a situation like this:
					// [%....[%...%]
					//
					startIndex = getStartIndex(startTag, content, startIndex, endIndex);

					// Get the tag in between
					//
					String tag = content.substring(startIndex + startTagLength, endIndex - endTagLength);

					// Only add tags that are not complements
					//
					if ( !tag.startsWith(compTag) )
					{
						retVal.add(tag);
					}
				}
			}
		}

		return retVal;
	}

	/**
	 * Figures out the start index to use for subVars method
	 *
	 * @param buff StringBuilder that contains end result
	 * @param content String content for which the substitution is performed
	 * @param startIndex Current start index
	 * @param endIndex Current end index
	 *
	 * @return final start index
	 */
	private static int getStartIndex (StringBuilder buff, String startTag, String content, int startIndex, int endIndex)
	{
		int startTagLength = startTag.length();

		// check if there's another start index inside. look for the last one.
		//
		int newStartIndex = content.indexOf(startTag, startIndex + startTagLength);

		// if there is a start index between original start index and end index,
		// append everything up to new startIndex and make new start index into the main start index
		//
		if ( newStartIndex != -1 && newStartIndex < endIndex )
		{
			buff.append(content.substring(startIndex, newStartIndex));
			startIndex = getStartIndex(buff, startTag, content, newStartIndex, endIndex);
		}

		// return start index
		//
		return startIndex;
	}

	/**
	 * Figures out the start index to use for getVars method
	 *
	 * @param content String content for which the substitution is performed
	 * @param startIndex Current start index
	 * @param endIndex Current end index
	 *
	 * @return final start index
	 */
	private static int getStartIndex (String startTag, String content, int startIndex, int endIndex)
	{
		int startTagLength = startTag.length();

		// check if there's another start index inside. look for the last one.
		//
		int newStartIndex = content.indexOf(startTag, startIndex + startTagLength);

		// if there is a start index between original start index and end index,
		// append everything up to new startIndex and make new start index into the main start index
		//
		if ( newStartIndex != -1 && newStartIndex < endIndex )
		{
			startIndex = getStartIndex(startTag, content, newStartIndex, endIndex);
		}

		// return start index
		//
		return startIndex;
	}

	/**
	 * Gets the time for the start of the day given a calendar object
	 *
	 * @param cal The calendar object to use when getting the start of the day,
	 *      this object gets modified to also reflect the start of the day
	 *
	 * @return The UNIX time representing the start of that day
	 */
	public static int getStartOfDay(Calendar cal)
	{
		cal.set(Calendar.HOUR, cal.getActualMinimum(Calendar.HOUR));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.AM_PM, cal.getActualMinimum(Calendar.AM_PM));

		return (int) ( cal.getTimeInMillis() / 1000 );
	}

	/**
	 * Gets the time for the end of the day given a calendar object
	 *
	 * @param cal The calendar object to use when getting the end of the day,
	 * this object gets modified to also reflect the end of the day
	 *
	 * @return The UNIX time representing the end of the day
	 */
	public static int getEndOfDay(Calendar cal)
	{
		cal.set(Calendar.HOUR, cal.getActualMaximum(Calendar.HOUR));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		cal.set(Calendar.AM_PM, cal.getActualMaximum(Calendar.AM_PM));

		return (int) ( cal.getTimeInMillis() / 1000 );
	}

	/**
	 * Converts a serializable object to a string that can be passed as an
	 * HTTP parameter
	 *
	 * @param obj The serializable object to encode as a parameter
	 *
	 * @return The stringified parameter to use
	 */
	public static String toHttpParam(Serializable obj)
	{
		// Encode the object as a Base64 string
		//
		String retVal = Base64.encodeObject(obj, Base64.GZIP | Base64.DONT_BREAK_LINES);

		// Replace the HTTP parameter special characters
		//
		retVal = retVal.replaceAll("\\+", "%2B");

		// Return the final string
		//
		return retVal;
	}

	/**
	 *  Encodes a URL for HTML form encoding.
	 *
	 * @param url The URL to encode
	 *
	 * @return The encoded URL
	 *
	 * @throws UnsupportedEncodingException if there was a problem encoding it
	 */
	public static String encodeURL(String url) throws UnsupportedEncodingException
	{
		if ( url != null )
		{
			return URLEncoder.encode(url, "UTF-8");
		}
		else
		{
			return "";
		}
	}

	/**
	 * Decodes a URL from previous encoding.
	 *
	 * @param url The URL to decode
	 *
	 * @return The decoded URL
	 *
	 * @throws UnsupportedEncodingException if there was a problem encoding it
	 */
	public static String decodeURL(String url) throws UnsupportedEncodingException
	{
		if ( url != null )
		{
			return URLDecoder.decode(url, "UTF-8");
		}
		else
		{
			return "";
		}
	}

	/**
	 *  Encodes an ASCII string with special characters for display in HTML.
	 *
	 * @param st
	 *            A string optionally containing standard java escape sequences.
	 * @return The translated string.
	 */
	public static String unescapeJavaString(String st)
	{
		StringBuilder sb = new StringBuilder(st.length());

		for ( int i = 0; i < st.length(); i++ )
		{
			char ch = st.charAt(i);
			if ( ch == '\\' )
			{
				char nextChar = ( i == st.length() - 1 ) ? '\\' : st
				                .charAt(i + 1);
				// Octal escape?
				if ( nextChar >= '0' && nextChar <= '7' )
				{
					String code = "" + nextChar;
					i++;
					if (( i < st.length() - 1 ) && st.charAt(i + 1) >= '0'
					    && st.charAt(i + 1) <= '7' )
					{
						code += st.charAt(i + 1);
						i++;
						if (( i < st.length() - 1 ) && st.charAt(i + 1) >= '0'
						    && st.charAt(i + 1) <= '7' )
						{
							code += st.charAt(i + 1);
							i++;
						}
					}
					sb.append((char) Integer.parseInt(code, 8));
					continue;
				}
				switch ( nextChar )
				{
					case '\\':
						ch = '\\';
						break;
					case 'b':
						ch = '\b';
						break;
					case 'f':
						ch = '\f';
						break;
					case 'n':
						ch = '\n';
						break;
					case 'r':
						ch = '\r';
						break;
					case 't':
						ch = '\t';
						break;
					case '\"':
						ch = '\"';
						break;
					case '\'':
						ch = '\'';
						break;
					// Hex Unicode: u????
					case 'u':
						if ( i >= st.length() - 5 )
						{
							ch = 'u';
							break;
						}
						int code = Integer.parseInt(
						        "" + st.charAt(i + 2) + st.charAt(i + 3)
						        + st.charAt(i + 4) + st.charAt(i + 5), 16);
						sb.append(Character.toChars(code));
						i += 5;
						continue;
				}
				i++;
			}
			sb.append(ch);
		}

		return sb.toString();
	}

	/**
	 * This method encodes an ASCII string with special characters for display in HTML.
	 *
	 * @param str The string to encode
	 *
	 * @return The encoded string
	 */
	public static String encodeHTML(String str)
	{
		StringBuilder retVal = new StringBuilder();
		for ( int i = 0; i < str.length(); i++ )
		{
			char c = str.charAt(i);
			if ( c > 127 || c == '"' || c == '<' || c == '>' )
			{
				retVal.append("&#");
				retVal.append((int) c);
				retVal.append(";");
			}
			else
			{
				retVal.append(c);
			}
		}

		return retVal.toString();
	}

	/**
	 * Decodes an HTML encoded string with special characters into an ASCII string.
	 *
	 * @param str The string to decodes
	 *
	 * @return The encoded string
	 */
	public static String decodeHTML(String str)
	{
		HashMap<String, Character> replaceStrs = new HashMap<>();

		replaceStrs.put("&cent;", (char) 162);
		replaceStrs.put("&pound;", (char) 163);
		replaceStrs.put("&yen;", (char) 165);
		replaceStrs.put("&sect;", (char) 167);
		replaceStrs.put("&amp;", '&');
		replaceStrs.put("&quot;", '"');
		replaceStrs.put("&AElig;", (char) 198);
		replaceStrs.put("&aelig;", (char) 230);
		replaceStrs.put("&OElig;", (char) 338);
		replaceStrs.put("&oelig;", (char) 339);
		replaceStrs.put("&Ccedil;", (char) 199);
		replaceStrs.put("&ccedil;", (char) 231);
		replaceStrs.put("&Agrave;", (char) 192);
		replaceStrs.put("&agrave;", (char) 224);
		replaceStrs.put("&Aacute;", (char) 193);
		replaceStrs.put("&aacute;", (char) 225);
		replaceStrs.put("&Egrave;", (char) 200);
		replaceStrs.put("&egrave;", (char) 232);
		replaceStrs.put("&Eacute;", (char) 201);
		replaceStrs.put("&eacute;", (char) 233);
		replaceStrs.put("&Igrave;", (char) 204);
		replaceStrs.put("&igrave;", (char) 236);
		replaceStrs.put("&Iacute;", (char) 205);
		replaceStrs.put("&iacute;", (char) 237);
		replaceStrs.put("&Ograve;", (char) 210);
		replaceStrs.put("&ograve;", (char) 242);
		replaceStrs.put("&Oacute;", (char) 211);
		replaceStrs.put("&oacute;", (char) 243);
		replaceStrs.put("&Ugrave;", (char) 217);
		replaceStrs.put("&ugrave;", (char) 249);
		replaceStrs.put("&Uacute;", (char) 218);
		replaceStrs.put("&uacute;", (char) 250);
		replaceStrs.put("&Yacute;", (char) 221);
		replaceStrs.put("&yacute;", (char) 253);
		replaceStrs.put("&Acirc;", (char) 194);
		replaceStrs.put("&acirc;", (char) 226);
		replaceStrs.put("&Atilde;", (char) 195);
		replaceStrs.put("&atilde;", (char) 227);
		replaceStrs.put("&Auml;", (char) 196);
		replaceStrs.put("&auml;", (char) 228);
		replaceStrs.put("&Aring;", (char) 197);
		replaceStrs.put("&aring;", (char) 229);
		replaceStrs.put("&Ecirc;", (char) 202);
		replaceStrs.put("&ecirc;", (char) 234);
		replaceStrs.put("&Euml;", (char) 203);
		replaceStrs.put("&euml;", (char) 235);
		replaceStrs.put("&Icirc;", (char) 206);
		replaceStrs.put("&icirc;", (char) 238);
		replaceStrs.put("&Iuml;", (char) 207);
		replaceStrs.put("&iuml;", (char) 239);
		replaceStrs.put("&Ocirc;", (char) 212);
		replaceStrs.put("&ocirc;", (char) 244);
		replaceStrs.put("&Otilde;", (char) 213);
		replaceStrs.put("&otilde;", (char) 245);
		replaceStrs.put("&Ouml;", (char) 214);
		replaceStrs.put("&ouml;", (char) 246);
		replaceStrs.put("&Ucirc;", (char) 219);
		replaceStrs.put("&ucirc;", (char) 251);
		replaceStrs.put("&Uuml;", (char) 220);
		replaceStrs.put("&uuml;", (char) 252);
		replaceStrs.put("&yuml;", (char) 255);
		replaceStrs.put("&frac14;", (char) 188);
		replaceStrs.put("&frac12;", (char) 189);
		replaceStrs.put("&frac34;", (char) 190);
		replaceStrs.put("&copy;", (char) 169);
		replaceStrs.put("&times;", (char) 215);
		replaceStrs.put("&divide;", (char) 247);
		replaceStrs.put("&reg;", (char) 174);
		replaceStrs.put("&para;", (char) 182);
		replaceStrs.put("&plusmn;", (char) 177);
		replaceStrs.put("&middot;", (char) 183);
		replaceStrs.put("&lt;", '<');
		replaceStrs.put("&gt;", '>');
		replaceStrs.put("&not;", (char) 172);
		replaceStrs.put("&curren;", (char) 164);
		replaceStrs.put("&brvbar;", (char) 166);
		replaceStrs.put("&deg;", (char) 176);
		replaceStrs.put("&acute;", (char) 180);
		replaceStrs.put("&uml;", (char) 168);
		replaceStrs.put("&macr;", (char) 175);
		replaceStrs.put("&cedil;", (char) 184);
		replaceStrs.put("&laquo;", (char) 171);
		replaceStrs.put("&raquo;", (char) 187);
		replaceStrs.put("&sup1;", (char) 185);
		replaceStrs.put("&sup2;", (char) 178);
		replaceStrs.put("&sup3;", (char) 179);
		replaceStrs.put("&ordf;", (char) 170);
		replaceStrs.put("&ordm;", (char) 186);
		replaceStrs.put("&iexcl;", (char) 161);
		replaceStrs.put("&iquest;", (char) 191);
		replaceStrs.put("&micro;", (char) 181);
		replaceStrs.put("&shy;", (char) 173);
		replaceStrs.put("&ETH;", (char) 208);
		replaceStrs.put("&eth;", (char) 240);
		replaceStrs.put("&Ntilde;", (char) 209);
		replaceStrs.put("&ntilde;", (char) 241);
		replaceStrs.put("&Oslash;", (char) 216);
		replaceStrs.put("&oslash;", (char) 248);
		replaceStrs.put("&szlig;", (char) 223);
		replaceStrs.put("&THORN;", (char) 222);
		replaceStrs.put("&thorn;", (char) 254);

		// Overwrite the same string over and over... there's got
		// to be a better way than this algorithm
		//
		String retVal = str;

		Set < Map.Entry < String, Character >> replaceValues = replaceStrs.entrySet();
		for ( Map.Entry<String, Character> entry : replaceValues )
		{
			retVal = retVal.replaceAll( entry.getKey(), String.valueOf(entry.getValue()) );
		}

		return retVal;
	}

	/**
	 * See if an e-mail address is valid or not
	 *
	 * @param email The e-mail address to validate
	 *
	 * @return true If it's valid, false otherwise
	 */
	public static boolean isValidEmail(String email)
	{
		try
		{
			// Try to instantiate an internet address
			//
			InternetAddress address = new InternetAddress(email, true);

			// We were successful in creating it
			//
			return true;
		}
		catch ( AddressException ae )
		{
			return false;
		}
	}

	/**
	 * Validate an e-mail address. If the e-mail is invalid,
	 * throw an exception
	 *
	 * @param email The e-mail address to validate
	 *
	 * @throws AddressException if the e-mail is invalid
	 */
	public static void validateEmail(String email) throws AddressException
	{
		// Try to instantiate an internet address
		//
		InternetAddress address = new InternetAddress(email, true);
	}

	/**
	 * Appends the offset and limit to the statement if either is specified
	 * as anything other than zero
	 *
	 * @param str The SQL statement
	 * @param offset The offset field
	 * @param limit The limit field
	 * @param orderby Which column to order by (null to disregard order)
	 * @param ascending Whether to order in ascending or descending order
	 *
	 * @return The resulting SQL statement
	 */
	public static String makeSQLwithOffset(String str, long offset, long limit, String orderby, boolean ascending)
	{
		StringBuilder sqlStmnt = new StringBuilder(str);

		// If the orderby column is present append the ordering
		//
		if ( orderby != null )
		{
			sqlStmnt.append(" ORDER BY ").append(sqlEscape(orderby));

			// If the user wants the order in descending order append
			// the DESC keyword
			//
			if ( !ascending )
			{
				sqlStmnt.append(" DESC");
			}
		}

		// If one of these two is not zero then we must
		// put in the limiting elements
		//
		if ( limit != 0 || offset != 0 )
		{
			sqlStmnt.append(" LIMIT ").append(limit);
			sqlStmnt.append(" OFFSET ").append(offset);
		}

		// Terminate the statement
		//
		sqlStmnt.append(";");

		// Return the statement
		//
		return sqlStmnt.toString();
	}

	/**
	 * Compares an MD5 hash to a raw string, by converting the raw string
	 * to its MD5 equivalent and then comparing byte by byte
	 *
	 * @param md5hash The MD5 hash to compare
	 * @param raw The raw string whose hash is to be compared
	 *
	 * @return Whether the raw string matches the MD5 hash given or not
	 *
	 * @throws NoSuchAlgorithmException if the MD5 algorithm is not found
	 */
	public static final boolean md5Compare(byte[] md5hash, String raw) throws NoSuchAlgorithmException
	{
		// Digest the raw string into MD5
		//
		byte[] rawMd5 = getMD5Hash(raw);

		// Check the lengths, and then compare byte by byte
		//
		boolean retVal = ( md5hash.length == rawMd5.length );
		for ( int i = 0; i < rawMd5.length && retVal; i++ )
		{
			retVal &= ( rawMd5[i] == md5hash[i] );
		}

		return retVal;
	}

	/**
	 * Decodes a hex string and converts every 2 characters to a byte
	 *
	 * @param hexStr The hex encoded MD5 string
	 *
	 * @return A byte array with the converted ASCII values
	 */
	public static final byte[] decodeHexMD5(String hexStr) throws NumberFormatException
	{
		// Only works on lower case letters
		//
		hexStr = hexStr.toLowerCase();

		// Half as many bytes out there
		//
		byte[] retVal = new byte[hexStr.length() / 2];

		// These are each individual ASCII characters of the string
		//
		byte[] charVals = hexStr.getBytes();

		// Go through the string 2 chars at a time
		//
		int j = 0;
		for ( int i = 0; i < ( charVals.length - 1 ); i += 2 )
		{
			byte b1 = charVals[i];
			byte b2 = charVals[i + 1];

			// The ASCII character here can be either 0-9 or a-f. If it's 0-9
			// we simply subtract from the base '0' character in order to arrive
			// at the numerical value ('0' - '0' = 0). If it's an a-f character
			// then we subtract from the base character 'a' and add 10 since the
			// letter a is equal to the decimal value of 10 ('a' - 'a' + 10 = 10)
			//
			b1 = ( b1 >= '0' && b1 <= '9' ) ? (byte)( b1 - (byte)'0' ) : (byte)( b1 - (byte)'a' + 10 );
			b2 = ( b2 >= '0' && b2 <= '9' ) ? (byte)( b2 - (byte)'0' ) : (byte)( b2 - (byte)'a' + 10 );


			// Store the value
			//
			retVal[j] = (byte)( b1 * 16 + b2 );

			// Increment the store
			//
			j++;
		}

		return retVal;
	}

	/**
	 * Returns a byte array representing the MD5 hash of the input string.
	 *
	 * @param raw The raw string whose MD5 hash is to be retrieved
	 *
	 * @return The byte array with the MD5 hash of the string
	 *
	 * @throws NoSuchAlgorithmException if the MD5 algorithm is not found
	 */
	public static byte[] getMD5Hash(String raw) throws NoSuchAlgorithmException
	{
		return getHash(raw, "MD5");
	}

	/**
	 * Returns a byte array representing the hash of the input string using
	 * a particular hashing algorithm.
	 *
	 * @param raw The raw string whose hash is to be retrieved
	 * @param algorithm The algorithm to use for hashing (i.e. MD5, SHA-256)
	 *
	 * @return The byte array with the hash of the string
	 *
	 * @throws NoSuchAlgorithmException if the algorithm is not found
	 */
	public static byte[] getHash(String raw, String algorithm) throws NoSuchAlgorithmException
	{
		// Get the message digest object for hashing
		//
		MessageDigest md = MessageDigest.getInstance(algorithm);

		// Initialize for digestion
		//
		md.reset();

		// Return the digest
		//
		return md.digest(raw.getBytes());
	}

	/**
	 * Returns a hex string representing the MD5 hash of the input string.
	 *
	 * @param raw The raw string whose MD5 hex string is to be retrieved
	 *
	 * @return The MD5 hex string
	 *
	 * @throws NoSuchAlgorithmException if the MD5 algorithm is not found
	 */
	public static String getMD5Hex(String raw) throws NoSuchAlgorithmException
	{
		return getHex(getMD5Hash(raw));
	}

	/**
	 * Returns a hex string representing the given bytes.
	 *
	 * @param bytes The bytes to convert
	 *
	 * @return The hex string
	 */
	public static final String getHex(byte[] bytes)
	{
		/*
		StringBuilder hexString = new StringBuilder();
		for ( int i = 0; i < bytes.length; i++ )
		{
			int currByte = 0xFF & bytes[i];

			String hexValue;
			if ( currByte < 0x10 )
			{
				hexValue = "0" + Integer.toHexString(currByte);
			}
			else
			{
				hexValue = Integer.toHexString(currByte);
			}

			hexString.append(hexValue);
		}

		return hexString.toString();
		*/
		return new String(Hex.encode(bytes));
	}

	/**
	 * This method attempts to retrieve a variable from the request. If
	 * the variable does not exist in the request, then it attempts to
	 * gather it from the session.
	 *
	 * @param request The servlet request to use when looking up parameters
	 * @param httpSession The session object to use when looking up parameters
	 * @param paramName The name of the parameter to retrieve
	 *
	 * @return The variable's value giving priority to the request over the session,
	 *      or null if the variable doesn't exist in either
	 */
	public static String requestOverSession(ServletRequest request, HttpSession httpSession, String paramName)
	{
		// First try the request
		//
		String retVal = request.getParameter(paramName);

		// If it didn't exist, then try the session
		//
		if ( retVal == null )
		{
			return (String)httpSession.getAttribute(paramName);
		}
		else
		{
			return retVal;
		}
	}

	/**
	 * Attempts to retrieve multiple variables from the request. If
	 * the variable does not exist in the request, then it attempts to
	 * gather it from the session.
	 *
	 * @param request The servlet request to use when looking up parameters
	 * @param httpSession The session object to use when looking up parameters
	 * @param paramName The name of the parameter to retrieve
	 *
	 * @return The variable's value giving priority to the request over the session,
	 *      or null if the variable doesn't exist in either
	 */
	public static String[] requestOverSessionValues(ServletRequest request, HttpSession httpSession, String paramName)
	{
		// First try the request
		//
		String[] retVal = request.getParameterValues(paramName);

		// If it didn't exist, then try the session
		//
		if ( retVal == null )
		{
			return ( String[] )httpSession.getAttribute(paramName);
		}
		else
		{
			return retVal;
		}
	}

	/**
	 * Removes any character sequence that would violate CDATA standards
	 *
	 * @param str The string to remove these characters from
	 *
	 * @return The string without these values
	 */
	public static String makeCDATA(String str)
	{
		return stripNonCDATA(forceEmptyStr(str).replaceAll("<!\\[CDATA\\[", "").replaceAll("\\]\\]>", ""));
	}

	/**
	 * Constructs a new string based on a given string repeated n times
	 *
	 * @param str The string to repeat
	 * @param n The number of times to repeat
	 *
	 * @return The str value repeated n times
	 */
	public static String repeatStr(String str, int n)
	{
		StringBuilder retVal = new StringBuilder(str.length() * n);

		for ( int i = 0; i < n; i++ )
		{
			retVal.append(str);
		}

		return retVal.toString();
	}

	/**
	 * Returns the greater of two values
	 *
	 * @param a The first value
	 * @param b The second value
	 * 
	 * @return The greater of two values
	 */
	public static int greater(int a, int b)
	{
		return (a < b) ? b : a;
	}

	/**
	 * Returns the lesser of two values
	 * 
	 * @param a The first value
	 * @param b The second value
	 * 
	 * @return The lesser of the two values
	 */
	public static int lesser(int a, int b)
	{
		return (a > b) ? b : a;
	}

	/**
	 *  Gets a file upload object from the request
	 *
	 * @param req The HTTP request object
	 * @param paramName The parameter name of the uploaded file
	 *
	 * @return A FileItem object containing the uploaded data file, or
	 *      null if the parameter was not found
	 *
	 * @throws IOException if there was an error retrieving the file
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static FileItem getFileUpload(HttpServletRequest req, String paramName) throws IOException
	{
		// This is where large files will be temporarily stored
		//
		DiskFileItemFactory diFactory = new DiskFileItemFactory();

		// Maximum size that'll be stored in memory
		//
		diFactory.setSizeThreshold(MAX_MEMORY_FILESIZE);

		// Set the temporary file repository
		//
		diFactory.setRepository(new File(UPLOAD_REPOSITORY));

		// Takes care of parsing the request
		//
		ServletFileUpload upload = new ServletFileUpload(diFactory);

		// Set the maximum file size
		//
		upload.setFileSizeMax(MAX_DISK_FILESIZE);

		try
		{
			// Now perform the parsing and storage
			//
			List fileItems = upload.parseRequest(req);

			// Traverse through all of them and search for the avatar parameter
			//
			Iterator it = fileItems.iterator();

			// Go through all elements and find the one we're interested in
			//
			while ( it.hasNext() )
			{
				// Get the next item
				//
				FileItem fi = (FileItem)it.next();

				// Find it?
				//
				if ( fi.getFieldName().equalsIgnoreCase(paramName))
				{
					// If so, return immediately
					//
					return fi;
				}
			}

			// Didn't find it
			//
			return null;
		}
		catch ( FileUploadException fue )
		{
			// Now wrap the exception message and throw it back up
			//
			throw new IOException(fue.getMessage());
		}
	}

	/**
	 * Gets a map of fileitem objects from the request
	 *
	 * @param req The HTTP request object
	 * @return A map of fileitem names to fileitem objects parsed from the request
	 *
	 * @throws IOException If there was a FileUploadException during the fileitem parse
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap<String, FileItem> getFileUploadMap(HttpServletRequest req) throws IOException
	{
		// This is where large files will be temporarily stored
		//
		DiskFileItemFactory diFactory = new DiskFileItemFactory();

		// Maximum size that'll be stored in memory
		//
		diFactory.setSizeThreshold(MAX_MEMORY_FILESIZE);

		// Set the temporary file repository
		//
		diFactory.setRepository(new File(UPLOAD_REPOSITORY));

		// Takes care of parsing the request
		//
		ServletFileUpload upload = new ServletFileUpload(diFactory);

		// Set the maximum file size
		//
		upload.setFileSizeMax(MAX_DISK_FILESIZE);

		// This is our return map
		//
		HashMap<String, FileItem> retMap = new HashMap<>();

		try
		{
			// Parse the request into a list of fileitems
			//
			List fileItems = upload.parseRequest(req);

			// Go through all of the fileitems if there are any
			//
			for ( Object o : fileItems )
			{
				FileItem fi = (FileItem) o;

				// Add it to the map
				//
				retMap.put(fi.getFieldName(), fi);
			}
		}
		catch ( FileUploadException fue )
		{
			throw new IOException(fue.getMessage());
		}

		return retMap;
	}

	/**
	 * Change the working directory and create it if it doesn't exist
	 *
	 * @param ftp The FTPClient object that is connected
	 * @param dir The directory to create and change into
	 *
	 * @return True if successful, false otherwise
	 *
	 * @throws IOException if there was an error sending the commands
	 */
	public static boolean changeFTPDirectory(FTPClient ftp, String dir) throws IOException
	{
		// First try to change ino the directory
		//
		if ( ftp.changeWorkingDirectory(dir) )
		{
			// The directory exists
			//
			return true;
		}
		else
		{
			// We couldn't change the directory so attempt to create it
			//
			if ( ftp.makeDirectory(dir) )
			{
				// See if we can change into it now
				//
				return ftp.changeWorkingDirectory(dir);
			}
			else
			{
				// Couldn't even create the directory
				//
				return false;
			}
		}
	}

	/**
	 * Returns the contents of a paritcular HTTP URL if encoded or the value passed.
	 *
	 * @param optVal The option value that is to be encoded. If this value starts
	 *      with [%url= then the value is treated as a URL. Otherwise, the string
	 *      is returned untouched
	 *
	 * @return The contents of the URL, the default string or null if there was
	 *      an error retreiving the contents of the URL.
	 */
	public static String getURLValue(String optVal)
	{
		if ( optVal == null )
		{
			return null;
		}
		else
		{
			int urlBegin = optVal.indexOf(URL_BEGIN);
			int urlEnd = optVal.indexOf("%]");
			if ( !isEmpty(optVal) && urlBegin >= 0 )
			{
				// It does, so try to get the contents of the
				// URL directly
				//
				try
				{
					// Get the URL value from the tag
					//
					String urlVal = optVal.substring( urlBegin + URL_BEGIN.length(), optVal.indexOf("%]") );

					// Finally return the value we've read
					//
					return makeCDATA( optVal.substring(0, urlBegin) + URLCache.getCachedURLContents(urlVal) + optVal.substring(urlEnd + 2, optVal.length()));
				}
				catch ( Exception e )
				{
					// Print out the error
					//
					e.printStackTrace();

					// Couldn't get the value for whatever reason
					// so we return null
					//
					return null;
				}
			}
			else
			{
				// Return the direct value
				//
				return optVal;
			}
		}
	}

	/**
	 * Gets a cookie by name from the array of cookies
	 *
	 * @param req The HTTP request object containing the cookies
	 * @param name The name of the cookie
	 *
	 * @return The cookie object with the given name
	 */
	public static Cookie getCookie(HttpServletRequest req, String name)
	{
		// Get all the cooies
		//
		Cookie[] cookies = req.getCookies();
		Cookie currCookie = null;

		// Go through all of them and find the right one
		//
		for ( int i = 0; cookies != null && i < cookies.length; i++ )
		{
			// See if this cookie is the one we're looking for
			//
			if ( cookies[i].getName().equalsIgnoreCase(name))
			{
				// We found it!
				//
				currCookie = cookies[i];
				break;
	}
		}

		return currCookie;
	}

	/**
	 * Removes a cookie from the response
	 *
	 * @param resp The HTTP Servlet response object setting the cookie
	 * @param cookie The cookie to remove
	 */
	public static void delCookie(HttpServletResponse resp, Cookie cookie)
	{
		// If there's no cookie, then don't do anything
		//
		if ( cookie != null )
		{
			// Expire it
			//
			cookie.setMaxAge(0);

			// Re-set it
			//
			resp.addCookie(cookie);
		}
	}

	public static String toCSV(Enumeration enumeration)
	{
		return toCSV(Collections.list(enumeration));
	}

	public static String toCSV(Collection a)
	{
		return toCSV(a, Object::toString);
	}

	public static <T extends Object> String toCSV(T[] a)
	{
		return toCSV(a, T::toString);
	}

	public static <T extends Object> String toCSV(T[] a, Stringifier<T> stringifier)
	{
		if ( a == null )
		{
			return "null";
		}
		else
		{
			StringBuilder retVal = new StringBuilder();

			int i = 0;
			for (T item : a )
			{
				if ( i != 0 )
				{
					retVal.append(",");
				}

				retVal.append(stringifier.toString(item));
				i++;
			}

			return retVal.toString();
		}
	}

	public static <T extends Object> String toCSV(Collection<T> a, Stringifier<T> stringifier)
	{
		if ( a == null )
		{
			return "null";
		}
		else
		{
			StringBuilder retVal = new StringBuilder();

			int i = 0;
			for (T item : a )
			{
				if ( i != 0 )
				{
					retVal.append(",");
				}

				retVal.append(stringifier == null || item == null ? "null"  : stringifier.toString(item));
				i++;
			}

			return retVal.toString();
		}
	}

	public static <T extends Object> String toCSV(Stack<T> stack, Stringifier<T> stringifier)
	{
		if ( stack == null )
		{
			return "null";
		}
		else
		{
			StringBuilder retVal = new StringBuilder();
			while ( !stack.isEmpty() )
			{
				retVal.append(stringifier.toString(stack.pop()));
				retVal.append(",");
			}
			retVal.deleteCharAt(retVal.length() - 1);
	
			return retVal.toString();
		}
	}

	/**
	 * Creates a comma separated String out of the given array
	 *
	 * @param a The array of ints
	 *
	 * @return The comma separated String
	 */
	public static String toCSV( int[] a )
	{
		StringBuilder sb = new StringBuilder();

		for ( int i = 0; i < a.length; i++ )
		{
			sb.append(String.valueOf(a[i]));

			// Don't put a comma on the end
			//
			if ( i < a.length - 1 )
			{
				sb.append(",");
			}
		}

		String retVal = sb.toString();
		return retVal;
	}

	/**
	 * Creates a comma separated String out of the given array
	 *
	 * @param a The array of Strings
	 *
	 * @return The comma separated String
	 */
	public static String toCSV(String[] a)
	{
		StringBuilder sb = new StringBuilder();

		for ( int i = 0; i < a.length; i++ )
		{
			sb.append(csvEscape(a[i]));

			// Don't put a comma on the end
			//
			if ( i < a.length - 1 )
			{
				sb.append(",");
			}
		}

		String retVal = sb.toString();
		return retVal;
	}

	/**
	 * Creates a comma separated String out of the given array
	 *
	 * @param a The array of Strings
	 *
	 * @return The comma separated String
	 */
	public static String toCSV( List<String> a )
	{
		StringBuilder sb = new StringBuilder();

		int length = a.size();
		for ( int i = 0; i < length; i++ )
		{
			sb.append(csvEscape(a.get(i)));

			// Don't put a comma on the end
			//
			if ( i < length - 1 )
			{
				sb.append(",");
			}
		}

		String retVal = sb.toString();
		return retVal;
	}

	/**
	 * Repeats a string n times and separates each occurence with separator. For example:
	 * repeatStr("a", "-", 5) would return "a-a-a-a-a"
	 *
	 * @param str The string to repeat
	 * @param sep The separator to use in between each item
	 * @param n Number of times to repeat
	 *
	 * @return The string repeated n times and separated.
	 */
	public static String repeatStr(String str, String sep, int n)
	{
		StringBuilder sb = new StringBuilder(( str.length() + sep.length()) * n);
		for ( int i = 0; i < n; i++ )
		{
			sb.append(str);
			if ( i > 0 )
			{
				sb.append(sep);
			}
		}

		return sb.toString();
	}

	/**
	 * Gets the local server's hostname
	 *
	 * @return The local server's hostname
	 */
	public static String getHostname()
	{
		try
		{
			return InetAddress.getLocalHost().getHostName();
		}
		catch ( UnknownHostException e )
		{
			// Return the error message instead
			//
			return e.getMessage();
		}
	}

	/**
	 * Get our current locale
	 * 
	 * @param request The incoming servlet request
	 * 
	 * @return The locale the request is in 
	 */
	public static Locale getLocale(HttpServletRequest request)
	{
		RequestContext rc = new RequestContext(request);
		return rc.getLocale();
	}

	/**
	 * Convert an epoch timestamp in seconds to a Date object
	 * 
	 * @param seconds The seconds to convert
	 * 
	 * @return A Date object representing the input timestamp
	 */
	public static Date toDate(int seconds)
	{
		Date date = new Date();
		date.setTime((long)seconds * 1000L);

		return date;
	}

	/**
	 * Adds commas in the right places of an integer
	 * before returning it as a string
	 *
	 * @param i The integer to stringify
	 * @param l The locale to use when formatting the number
	 *
	 * @return The integer with the commas added
	 */
	public static String addCommas(long i, Locale l)
	{
		// Need an instance of the number format class
		//
		// Use the station locale to ensure that
		// the right character is used
		//
		NumberFormat commas = NumberFormat.getInstance(l);

		return commas.format(i);
	}

	/**
	 * Formats the decimal number given in the locale's currency
	 *
	 * @param d The number to stringify
	 * @param l The locale to use when formatting the number
	 *
	 * @return The float with the proper locale currency
	 */
	public static String formatCurrency(double d, Locale l)
	{
		// Need an instance of the number format class
		//
		// Use the station locale to ensure that
		// the right character is used
		//
		NumberFormat currency = NumberFormat.getCurrencyInstance(l);

		return currency.format(d);
	}

	/**
	 * Rounds the decimal number given
	 *
	 * @param d The number to stringify
	 * @param precision The number of digits after the decimal point
	 * @param l The locale to use when formatting the number
	 *
	 * @return The float with the proper rounding
	 */
	public static String formatDouble(double d, int precision, Locale l)
	{
		// Need an instance of the number format class
		//
		// Use the station locale to ensure that
		// the right character is used
		//
		NumberFormat roundNf = NumberFormat.getNumberInstance(l);
		roundNf.setMinimumFractionDigits(precision);
		roundNf.setMaximumFractionDigits(precision);

		return roundNf.format(d);
	}

	/**
	 * Adds commas in the right places of a double
	 * before returning it as a string
	 *
	 * @param d The double to stringify
	 * @param l The locale to use when formatting the number
	 *
	 * @return The integer with the commas added
	 */
	public static String addCommas(double d, Locale l)
	{
		// Need an instance of the number format class
		//
		// Use the station locale to ensure that
		// the right character is used
		//
		NumberFormat commas = NumberFormat.getInstance(l);

		return commas.format(d);
	}

	/**
	 * Given a string, extract all integers in that string
	 * 
	 * @param str The string to analyze
	 * 
	 * @return The set of integers found  in the string
	 */
	public static Integer[] extractIntegers(String str)
	{
		Matcher m = INTEGER_PATTERN.matcher(str);

		ArrayList<Integer> integers = new ArrayList<>();
		while ( m.find() )
		{
			integers.add(Integer.parseInt(m.group()));
	}

		Integer[] retVal = new Integer[integers.size()];
		return integers.toArray(retVal);
	}
	


	/**
	 * Reads an input stream's contents and saves it to a file
	 *
	 * @param in The input stream
	 *
	 * @return The file whose contents contain the input stream's value
	 *
	 * @throws IOException
	 */
	public static File spoolToFile(InputStream in) throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			File tmpFile = File.createTempFile("Utils", ".tmp");
			tmpFile.deleteOnExit();

			fos = new FileOutputStream(tmpFile);

			// Read in the entire file, this could be potentially large
			// but we rely on the callers to ensure sizes are reasonable
			//
			byte[] tmpBuffer = new byte[MAX_MEMORY_FILESIZE];
			boolean done;
			do
			{
				int bytesRead = in.read(tmpBuffer);
				done = ( bytesRead < 0 );

				if ( !done )
				{
					fos.write(tmpBuffer, 0, bytesRead);
				}
			}
			while ( !done );

			return tmpFile;
		}
		finally
		{
			if ( fos != null )
			{
				fos.close();
			}

			in.close();
		}
	}

	/**
	 * Spools an input stream's contents to a byte array
	 *
	 * @param in The input stream
	 *
	 * @return The byte array (at most MAX_MEMORY_FILESIZE bytes)
	 *
	 * @throws IOException
	 */
	public static byte[] spoolToByteArray(InputStream in) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] tmpBuffer = new byte[MAX_MEMORY_FILESIZE];
		boolean done;
		do
		{
			int bytesRead = in.read(tmpBuffer);
			done = ( bytesRead < 0 );

			if ( !done )
			{
				baos.write(tmpBuffer);
			}
		}
		while ( !done );


		return baos.toByteArray();
	}

	public static < K extends Comparable<? super K>, V > Map<K, V> sortMap(Map<K, V> map)
	{
		Set<K> keySet = map.keySet();
		List<K> keys = new ArrayList<>(keySet.size());
		keys.addAll(keySet);

		Collections.sort(keys);

		LinkedHashMap<K, V> retVal = new LinkedHashMap<>(map.size());
		keys.stream().forEach( (currKey) ->
		   {
			retVal.put(currKey, map.get(currKey));
		});

		return retVal;
	}

	/**
	 * Create a map from the arguments given in the form of args[i] => args[i+1]
	 *
	 * @param <U> The key type
	 * @param <T> The value type
	 * @param args The arguments to convert to a map in the form of U,T,U,T,U,T...
	 *
	 * @return The map containing the argument mappings
	 */
	public static <U extends Object, T extends Object> Map<U,T> makeMap(Object... args)
	{
		Map<U,T> retVal = new HashMap<>();

		if ( args != null && args.length > 1 )
		{
			for ( int i = 0; i < args.length && i + 1 < args.length; i += 2 )
			{
				retVal.put((U)args[i], (T)args[i+1]);
			}
		}

		return retVal;
	}

	/*
	protected static void testVars(String[] args) throws IOException
	{
		HashMap<String, Object> attrs = new HashMap<>();

		attrs.put("username", "hamid");
		attrs.put("nonmembers", false);
		attrs.put("members", true);
		attrs.put("points", "100 Points");
		attrs.put("outer", "Outer Tag");

		try (FileReader fr = new FileReader("x"))
		{
			char[] cBuff = new char[4096];
			int i = fr.read(cBuff);

			// Convert the chars to a string
		        //
			// String temp = "[%members%]This tag contains a /members/ in it[%/members%]";
			String temp = new String(cBuff);

			System.out.println("Subvars gives '" + subVars(attrs, temp) + "' w/" + i + " bytes read");
		}
	}
	*/
}
