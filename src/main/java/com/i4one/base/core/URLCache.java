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

import static com.i4one.base.core.Utils.currentTimeSeconds;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;


/**
 * This class cache's the contents of a URL for up to a set amount of time.
 *
 * @author Hamid Badiozamani
 */
public class URLCache
{
	/**
	 *  The maximum amount of time to cache a URL's contents in seconds
	 */
	public static final int CACHE_TTL = 5 * 60;

	/**
	 *  The maximum number of cache elements to hold
	 */
	public static final int MAX_ELEMENTS = 100;

	// The singleton instance
	//
	private static final URLCache instance;

	static
	{
		instance = new URLCache(MAX_ELEMENTS);
	}

	private final LRUMap < String, Pair < Integer, String >> cache;

	/**
	 *  The private constructor
	 */
	private URLCache(int maxElems)
	{
		cache = new LRUMap<>(maxElems);
	}

	private LRUMap<String, Pair<Integer, String> > getCache()
	{
		return cache;
	}

	/**
	 * Gets a URLs contents
	 *
	 * @param urlVal The URL whose contents we are to retrieve
	 * @return The contents at the given URL
	 *
	 * @throws IOException if there was an error retrieving the URL's contents
	 * @throws MalformedURLException if there was an error with the URL's syntax
	 */
	public static String getURLContents(String urlVal) throws java.io.IOException, java.net.MalformedURLException
	{
		// Now attempt to open a connection to that URL
		//
		URL url = new URL( urlVal );
		URLConnection urlConn = url.openConnection();

		// Get the input stream of the connection
		//
		InputStream urlIn = urlConn.getInputStream();

		// Now gather all the content up
		//
		StringBuilder retVal = new StringBuilder();

		// Read byte-by-byte
		//
		for ( int c = urlIn.read(); c > -1; c = urlIn.read() )
		{
			retVal.append((char)c);
		}

		return retVal.toString();
	}

	/**
	 * Gets a URLs contents by first checking the cache
	 *
	 * @param url The URL whose contents we are to retrieve
	 * @return The cached contents at the given URL
	 *
	 * @throws IOException if there was an error retrieving the URL's contents
	 * @throws MalformedURLException if there was an error with the URL's syntax
	 */
	public static String getCachedURLContents(String url) throws java.io.IOException, java.net.MalformedURLException
	{
		// First look in the map
		//
		Pair<Integer, String> mapEntry = instance.getCache().get(url);

		// See if there's a valid entry
		//
		if ( mapEntry != null &&
		     mapEntry.getKey() >= currentTimeSeconds() )
		{
			// It's valid, use the cache
			//
			return mapEntry.getValue();
		}
		else
		{
			// Either there's no entry or it's out of date
			//
			String retVal = getURLContents(url);

			// Set the expiration time to CACHE_TTL seconds from now
			//
			mapEntry = new Pair<>(currentTimeSeconds() * CACHE_TTL, retVal );

			instance.getCache().put(url, mapEntry);

			// Return the contents
			//
			return retVal;
		}
	}
}
