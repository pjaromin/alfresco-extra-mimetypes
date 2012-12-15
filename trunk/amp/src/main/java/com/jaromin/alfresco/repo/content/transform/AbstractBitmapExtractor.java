package com.jaromin.alfresco.repo.content.transform;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.ArrayUtils;

public abstract class AbstractBitmapExtractor implements BitmapExtractor {

	/**
	 * Reads through the file sequentially searching for the sequence. If found it reads that 
	 * header fully, positioning the next read for the byte immediately following the header
	 * and returns true. If none found, the file will be entirely consumed and method returns false.
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected boolean nextMatch(byte[] seq, InputStream in) throws IOException {
		in = buffer(in);
		int b = 0;
		while((b = in.read()) != -1) {
			if (matches(seq, in, b)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Check if the next series of bytes from the stream matches the sequence.
	 * The int 'b' is the most recently-read byte and the input stream is positioned
	 * to read the byte immediately following 'b'.
	 * If the match fails, the stream is reset. If it matches, the stream is NOT reset.
	 * @param seq
	 * @param in
	 * @param b
	 * @return
	 * @throws IOException
	 */
	protected boolean matches(byte[] seq, InputStream in, int b) throws IOException {
		if (((byte)b) == seq[0]) {
			// potential match, read next 7 bytes
			byte[] buf = new byte[seq.length - 1];
			in.mark(buf.length);
			if (in.read(buf) == -1) {
				in.reset();
				return false;
			}
			else {
				if (ArrayUtils.isEquals(buf,
						ArrayUtils.subarray(seq, 1, seq.length))) { // last 7 bytes
					return true;
				}
				in.reset(); // rewind
			}
		}
		return false;
	}

	protected InputStream buffer(InputStream in) {
		// Wrap with buffer if needed...
		if (!(in instanceof BufferedInputStream)) {
			in = new BufferedInputStream(in);
		}
		return in;
	}
}
