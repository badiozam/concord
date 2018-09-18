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

import static com.i4one.base.core.Base64.encode3to4;
import static com.i4one.base.core.Base64.decode4to3;
import java.io.FilterInputStream;
import java.io.InputStream;

/**
 *  A {@link Base64.InputStream} will read data from another
 *         <tt>java.io.InputStream</tt>, given in the constructor,
 *         and encode/decode to/from Base64 notation on the fly.
 *                 @see Base64
 *         @since 1.3
 */
public class Base64InputStream extends FilterInputStream
{
	private boolean encode;

	/**
	 *  Encoding or decoding
	 */
	private int position;

	/**
	 *  Current position in the buffer
	 */
	private byte[] buffer;

	/**
	 *  Small buffer holding converted data
	 */
	private int bufferLength;

	/**
	 *  Length of buffer (3 or 4)
	 */
	private int numSigBytes;

	/**
	 *  Number of meaningful bytes in the buffer
	 */
	private int lineLength;

	private boolean breakLines;

	/**
	 *  Constructs a {@link Base64.InputStream} in DECODE mode.
	 *                                     @param in the <tt>java.io.InputStream</tt> from which to read data.
	 *                     @since 1.3
	 */
	public Base64InputStream(InputStream in)
	{
		this( in, Base64.DECODE );
	}

	/**
	 *  Constructs a {@link Base64.InputStream} in
	 *                     either ENCODE or DECODE mode.
	 *                     <p>
	 *                     Valid options:<pre>
	 *                       ENCODE or DECODE: Encode or Decode as data is read.
	 *                       DONT_BREAK_LINES: don't break lines at 76 characters
	 *                         (only meaningful when encoding)
	 *                         <i>Note: Technically, this makes your encoding non-compliant.</i>
	 *                     </pre>
	 *                     <p>
	 *                     Example: <code>new Base64.InputStream( in, Base64.DECODE )</code>
	 *                                                     @param in the <tt>java.io.InputStream</tt> from which to read data.
	 *                     @param options Specified options
	 *                     @see Base64#ENCODE
	 *                     @see Base64#DECODE
	 *                     @see Base64#DONT_BREAK_LINES
	 *                     @since 2.0
	 */
	public Base64InputStream(InputStream in, int options)
	{
		super( in );
		this.breakLines = ( options & Base64.DONT_BREAK_LINES ) != Base64.DONT_BREAK_LINES;
		this.encode = ( options & Base64.ENCODE ) == Base64.ENCODE;
		this.bufferLength = encode ? 4 : 3;
		this.buffer = new byte[bufferLength];
		this.position = -1;
		this.lineLength = 0;
	}

	/**
	 *  Reads enough of the input stream to convert to/from Base64 and returns the next byte.
	 * 
	 * @return next byte
	 * @throws java.io.IOException
	 */
	@Override
	public int read() throws java.io.IOException
	{
		// Do we need to get data?
		if ( position < 0 )
		{
			if ( encode )
			{
				byte[] b3 = new byte[3];
				int numBinaryBytes = 0;
				for ( int i = 0; i < 3; i++ )
				{
					try
					{
						int b = in.read();

						// If end of stream, b is -1.
						if ( b >= 0 )
						{
							b3[i] = (byte)b;
							numBinaryBytes++;
						}         // end if: not end of stream
					}         // end try: read
					catch ( java.io.IOException e )
					{
						// Only a problem if we got no data at all.
						if ( i == 0 )
						{
							throw e;
						}
					}         // end catch
				}         // end for: each needed input byte

				if ( numBinaryBytes > 0 )
				{
					encode3to4( b3, 0, numBinaryBytes, buffer, 0 );
					position = 0;
					numSigBytes = 4;
				}         // end if: got data
				else
				{
					return -1;
				}         // end else
			}         // end if: encoding
			          // Else decoding
			else
			{
				byte[] b4 = new byte[4];
				int i;
				for ( i = 0; i < 4; i++ )
				{
					// Read four "meaningful" bytes:
					int b = 0;
					do
					{
						b = in.read();
					}
					while ( b >= 0 && Base64.DECODABET[b & 0x7f] <= Base64.WHITE_SPACE_ENC );

					if ( b < 0 )
					{
						break;         // Reads a -1 if end of stream
					}
					b4[i] = (byte)b;
				}         // end for: each needed input byte

				if ( i == 4 )
				{
					numSigBytes = decode4to3( b4, 0, buffer, 0 );
					position = 0;
				}         // end if: got four characters
				else if ( i == 0 )
				{
					return -1;
				}         // end else if: also padded correctly
				else
				{
					// Must have broken out from above.
					throw new java.io.IOException( "Improperly padded Base64 input." );
				}         // end
			}         // end else: decode
		}         // end else: get data

		// Got data?
		if ( position >= 0 )
		{
			// End of relevant data?
			if ( /*!encode &&*/ position >= numSigBytes )
			{
				return -1;
			}

			if ( encode && breakLines && lineLength >= Base64.MAX_LINE_LENGTH )
			{
				lineLength = 0;
				return '\n';
			}         // end if
			else
			{
				lineLength++;         // This isn't important when decoding
				                      // but throwing an extra "if" seems
				                      // just as wasteful.

				int b = buffer[position++];

				if ( position >= bufferLength )
				{
					position = -1;
				}

				return b & 0xFF;         // This is how you "cast" a byte that's
				                         // intended to be unsigned.
			}         // end else
		}         // end if: position >= 0
		          // Else error
		else
		{
			// When JDK1.4 is more accepted, use an assertion here.
			throw new java.io.IOException( "Error in Base64 code reading stream." );
		}         // end else
	}

	/**
	 * Calls {@link #read()} repeatedly until the end of stream is reached
	 * or <var>len</var> bytes are read. Returns number of bytes read into array or -1 if end of stream is encountered.
	 * 
	 * @param dest array to hold values
	 * @param off offset for array
	 * @param len max number of bytes to read into array
	 * 
	 * @return bytes read into array or -1 if end of stream is encountered.
	 * 
	 * @throws java.io.IOException
	 *                     @since 1.3
	 */
	@Override
	public int read(byte[] dest, int off, int len) throws java.io.IOException
	{
		int i;
		int b;
		for ( i = 0; i < len; i++ )
		{
			b = read();

			// if( b < 0 && i == 0 )
			//    return -1;

			if ( b >= 0 )
			{
				dest[off + i] = (byte)b;
			}
			else if ( i == 0 )
			{
				return -1;
			}
			else
			{
				break;         // Out of 'for' loop
			}
		}         // end for: each byte read

		return i;
	}
}

