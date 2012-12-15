package com.jaromin.alfresco.repo.content.transform;

/**
 * 
 * @author pjaromin
 *
 */
public class PngExtractor extends AbstractDelimitedBitmapExtractor {

	public static final byte[] PNG_HEADER = { -119, 80, 78, 71,  13, 10, 26,   10 };
	public static final byte[] PNG_FOOTER = {   73, 69, 78, 68, -82, 66, 96, -126 };

	@Override
	byte[] getHeaderSequence() {
		return PNG_HEADER;
	}

	@Override
	byte[] getFooterSequence() {
		return PNG_FOOTER;
	}

}
