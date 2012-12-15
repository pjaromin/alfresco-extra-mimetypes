package com.jaromin.alfresco.repo.content.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Extracts Windows bitmaps.
 * First 2 bytes identify the bitmap - for Windows files this is "BM".
 * The following 4 bytes are the size of the file in bytes.
 * See: http://en.wikipedia.org/wiki/BMP_file_format
 * @author pjaromin
 *
 */
public class WindowsBmpExtractor extends AbstractDelimitedBitmapExtractor {

	private static byte[] BMP_HEADERMARKER = new byte[]{ 66, 77 };

	/*
	 * (non-Javadoc)
	 * @see com.jaromin.alfresco.repo.content.transform.AbstractDelimitedBitmapExtractor#getHeaderSequence()
	 */
	@Override
	byte[] getHeaderSequence() {
		return BMP_HEADERMARKER;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jaromin.alfresco.repo.content.transform.AbstractBitmapExtractor#matches(byte[], java.io.InputStream, int)
	 */
	protected boolean matches(byte[] seq, InputStream in, int b) throws IOException {
		if (super.matches(seq, in, b)) {
			// need to go one step beyond and see if we at least have enough
			// bytes remaining in the file to match the amount specified in the header.
			// Clearly this isn't even remotely enough, but it's as far as I'm going now
			// and seems to be sufficient for practical purposes...
			long size = bytesSize(in);
			int needed = (int)size - BMP_HEADERMARKER.length;
			System.out.println("NEEDED = " + needed + " AVAILABLE: " + in.available() + " - " + (in.available() >= needed));
			return size > 0 && in.available() >= needed; // assume the entire bmp is 'available.' 
		}
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.jaromin.alfresco.repo.content.transform.AbstractDelimitedBitmapExtractor#extractBitmap(int, java.io.InputStream, java.io.OutputStream)
	 */
	@Override
	public boolean extractBitmap(int n, InputStream in, OutputStream out)
			throws IOException {
		int count = 0;
		while (nextMatch(BMP_HEADERMARKER, in)) {
			if (++count == n) {
				// This is the one we're after. Read in the PNG until the PNG_FOOTER...
				// Now read in the next 4 bytes which will tell us the size of the file:
				byte[] size = new byte[4];
				int read = in.read(size);
				if (read < 4) {
					return false;
				}
				// Now, read the next 'size' bytes
				int len = ByteBuffer.wrap(size).getInt();
				byte[] buf = new byte[len];
				if (in.read(buf) == len) {
					out.write(BMP_HEADERMARKER);
					out.write(size);
					out.write(buf);
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Assume we're positioned after the 'BM' header.
	 * Reads the size and resets the pointer after read.
	 * @param in
	 * @return
	 * @throws IOException 
	 */
	protected long bytesSize(InputStream in) throws IOException {
		byte[] size = new byte[4];
		in.mark(size.length);
		int read = in.read(size);
		in.reset();
		if (read < 4) {
			return -1;
		}

		System.out.println("Size: " + size[0] + " " + size[1] + " " + size[2] + " " + size[3]);
		long len = 0;
		for (int i = 0; i < size.length; i++)
		{
		   len += ((long) size[i] & 0xffL) << (8 * i);
		}
		return len;
	}

	/*
	 * (non-Javadoc)
	 * @see com.jaromin.alfresco.repo.content.transform.AbstractDelimitedBitmapExtractor#getFooterSequence()
	 */
	@Override
	byte[] getFooterSequence() {
		// Not used.
		return null;
	}

}
