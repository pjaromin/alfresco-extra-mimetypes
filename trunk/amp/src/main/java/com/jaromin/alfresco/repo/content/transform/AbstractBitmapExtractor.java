/**
 * Copyright 2012 Patrick Jaromin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.jaromin.alfresco.repo.content.transform;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 * @author pjaromin
 *
 */
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
