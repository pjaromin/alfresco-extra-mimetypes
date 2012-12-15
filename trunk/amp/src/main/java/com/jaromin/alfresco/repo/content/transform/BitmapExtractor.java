package com.jaromin.alfresco.repo.content.transform;

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
