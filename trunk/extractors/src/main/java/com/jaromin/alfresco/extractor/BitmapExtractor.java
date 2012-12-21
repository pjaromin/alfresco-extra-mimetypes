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
package com.jaromin.alfresco.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface for extracting bitmaps from binary files. Useful for extracting 
 * thumbnails embedded within various file formats.
 * @author pjaromin
 *
 */
public interface BitmapExtractor {

	/**
	 * Provides a count of the bitmap identified in the input stream.
	 * @param in
	 * @return
	 */
	int countBitmaps(InputStream in) throws IOException;
	
	/**
	 * Extracts the n-th bitmap from the input stream and writes to the
	 * output stream. Returns <tt>true</tt> if a bitmap was extracted, 
	 * <tt>false</tt> otherwise.
	 * @param n
	 * @param in
	 * @param out
	 * @return
	 */
	boolean extractBitmap(int n, InputStream in, OutputStream out) throws IOException;

}
