package com.jaromin.alfresco.repo.content.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tika.io.IOUtils;

/**
 * Abstract extractor that looks for a header and footer signature and extracts
 * including the header and footer.
 * @author pjaromin
 *
 */
public abstract class AbstractDelimitedBitmapExtractor extends AbstractBitmapExtractor {

	abstract byte[] getHeaderSequence();
	
	abstract byte[] getFooterSequence();
	
	/**
	 * Convenience method for counting from a file.
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public int countBitmaps(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			return countBitmaps(in);
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jaromin.alfresco.repo.content.transform.BitmapExtractor#countBitmaps(java.io.InputStream)
	 */
	@Override
	public int countBitmaps(InputStream in) throws IOException {
		byte[] seq = getHeaderSequence();
		int count = 0;
		in = buffer(in);
		while (nextMatch(seq, in)) {
			count++;
		}

		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see com.jaromin.alfresco.repo.content.transform.BitmapExtractor#extractBitmap(int, java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public boolean extractBitmap(int n, InputStream in, OutputStream out) throws IOException {
		byte[] header = getHeaderSequence();
		byte[] footer = getFooterSequence();

		in = buffer(in);
		int count = 0;
		while (nextMatch(header, in)) {
			if (++count == n) {
				// This is the one we're after. Read in the PNG until the PNG_FOOTER...
				out.write(header);
				int b = 0;
				while((b = in.read()) != -1) {
					if (matches(footer, in, b)) {
						// Matched footer. Stream not reset so
						// write footer and exit.
						out.write(footer);
						return true;
					}
					out.write(b);
				}
				
			}
		}
		return false;
	}

}
