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
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *  A {@link Base64.OutputStream} will write data to another
 *         <tt>java.io.OutputStream</tt>, given in the constructor,
 *         and encode/decode to/from Base64 notation on the fly.
 *                 @see Base64
 *         @since 1.3
 */
public class Base64OutputStream extends FilterOutputStream
{
	private boolean encode;

	private int position;

	private byte[] buffer;

	private int bufferLength;

	private int lineLength;

	private boolean breakLines;

	private byte[] b4;

	/**
	 *  Scratch used in a few places
	 */
	private boolean suspendEncoding;

	/**
	 *  Constructs a {@link Base64.OutputStream} in ENCODE mode.
	 *                                     @param out the <tt>java.io.OutputStream</tt> to which data will be written.
	 *                     @since 1.3
	 */
	public Base64OutputStream(OutputStream out)
	{
		this( out, Base64.ENCODE );
	}

	/**
	 *  Constructs a {@link Base64.OutputStream} in
	 *                     either ENCODE or DECODE mode.
	 *                     <p>
	 *                     Valid options:<pre>
	 *                       ENCODE or DECODE: Encode or Decode as data is read.
	 *                       DONT_BREAK_LINES: don't break lines at 76 characters
	 *                         (only meaningful when encoding)
	 *                         <i>Note: Technically, this makes your encoding non-compliant.</i>
	 *                     </pre>
	 *                     <p>
	 *                     Example: <code>new Base64.OutputStream( out, Base64.ENCODE )</code>
	 *                                     @param out the <tt>java.io.OutputStream</tt> to which data will be written.
	 *                     @param options Specified options.
	 *                     @see Base64#ENCODE
	 *                     @see Base64#DECODE
	 *                     @see Base64#DONT_BREAK_LINES
	 *                     @since 1.3
	 */
	public Base64OutputStream(OutputStream out, int options)
	{
		super( out );
		this.breakLines = ( options & Base64.DONT_BREAK_LINES ) != Base64.DONT_BREAK_LINES;
		this.encode = ( options & Base64.ENCODE ) == Base64.ENCODE;
		this.bufferLength = encode ? 3 : 4;
		this.buffer = new byte[bufferLength];
		this.position = 0;
		this.lineLength = 0;
		this.suspendEncoding = false;
		this.b4 = new byte[4];
	}

	/**
	 * Writes the byte to the output stream after converting to/from Base64
	 * notation. When encoding, bytes are buffered three at a time before
	 * the output stream actually gets a write() call. When decoding, bytes
	 * are buffered four at a time.
	 * 
	 * @param theByte the byte to write
	 * 
	 * @throws IOException
	 */
	@Override
	public void write(int theByte) throws IOException
	{
		// Encoding suspended?
		if ( suspendEncoding )
		{
			super.out.write( theByte );
			return;
		}         // end if: supsended

		// Encode?
		if ( encode )
		{
			buffer[position++] = (byte)theByte;
			if ( position >= bufferLength )         // Enough to encode.
			{
				out.write( encode3to4( b4, buffer, bufferLength ) );

				lineLength += 4;
				if ( breakLines && lineLength >= Base64.MAX_LINE_LENGTH )
				{
					out.write( Base64.NEW_LINE );
					lineLength = 0;
				}         // end if: end of line

				position = 0;
			}         // end if: enough to output
		}         // end if: encoding
		          // Else, Decoding
		else
		{
			// Meaningful Base64 character?
			if ( Base64.DECODABET[theByte & 0x7f] > Base64.WHITE_SPACE_ENC )
			{
				buffer[position++] = (byte)theByte;
				if ( position >= bufferLength )         // Enough to output.
				{
					int len = decode4to3( buffer, 0, b4, 0 );
					out.write( b4, 0, len );
					// out.write( Base64.decode4to3( buffer ) );
					position = 0;
				}         // end if: enough to output
			}         // end if: meaningful base64 character
			else if ( Base64.DECODABET[theByte & 0x7f] != Base64.WHITE_SPACE_ENC )
			{
				throw new IOException( "Invalid character in Base64 data." );
			}         // end else: not white space either
		}         // end else: decoding
	}

	/**
	 * Calls {@link #write(int)} repeatedly until <var>len</var> bytes are written.
	 * 
	 * @param theBytes array from which to read bytes
	 * @param off offset for array
	 * @param len max number of bytes to read into array
	 * 
	 * @throws IOException
	 */
	@Override
	public void write(byte[] theBytes, int off, int len) throws IOException
	{
		// Encoding suspended?
		if ( suspendEncoding )
		{
			super.out.write( theBytes, off, len );
			return;
		}         // end if: supsended

		for ( int i = 0; i < len; i++ )
		{
			write( theBytes[off + i] );
		}         // end for: each byte written

	}

	/**
	 * This pads the buffer without closing the stream.
	 * 
	 * @throws IOException
	 */
	public void flushBase64() throws IOException
	{
		if ( position > 0 )
		{
			if ( encode )
			{
				out.write( encode3to4( b4, buffer, position ) );
				position = 0;
			}         // end if: encoding
			else
			{
				throw new IOException( "Base64 input not properly padded." );
			}         // end else: decoding
		}         // end if: buffer partially full
	}

	/**
	 * Flushes and closes (I think, in the superclass) the stream.
	 * 
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException
	{
		// 1. Ensure that pending characters are written
		flushBase64();

		// 2. Actually close the stream
		// Base class both flushes and closes.
		super.close();

		buffer = null;
		out = null;
	}

	/**
	 * Suspends encoding of the stream. May be helpful if you need to embed
	 * a piece of base64 encoded data in a stream.
	 * 
	 * @throws IOException
	 */
	public void suspendEncoding() throws IOException
	{
		flushBase64();
		this.suspendEncoding = true;
	}

	/**
	 * Resumes encoding of the stream. May be helpful if you need to embed a piece of
	 * base64 encoded data in a stream.
	 */
	public void resumeEncoding()
	{
		this.suspendEncoding = false;
	}
}

