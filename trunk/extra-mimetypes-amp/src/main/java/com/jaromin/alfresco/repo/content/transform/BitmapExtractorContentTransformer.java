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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.io.IOUtils;

/**
 * Extracts the embedded PNG file from a SketchUp file.
 * @author pjaromin
 *
 */
public class BitmapExtractorContentTransformer extends AbstractContentTransformer2 {

	private static final String MIMETYPE_SKETCHUP = "application/vnd.sketchup.skp";
	
	private static final String MIMETYPE_PNG = "image/png";
	
	/**
	 * A Map of extractors keyed by mimetype.
	 */
	private Map<String, BitmapExtractor> extractors;
	
	/**
	 * The index of the bitmap to return. This is for formats that may
	 * contain multiple bitmaps where we want the n-th one for the thumbnail/preview.
	 * Defaults to '1', the first image found.
	 */
	private int index = 1;	
	
	/*
	 * (non-Javadoc)
	 * @see org.alfresco.repo.content.transform.AbstractContentTransformerLimits#isTransformable(java.lang.String, java.lang.String, org.alfresco.service.cmr.repository.TransformationOptions)
	 */
	@Override
	public boolean isTransformableMimetype(String sourceMimetype,
			String targetMimetype, TransformationOptions options) {
		return extractors.containsKey(sourceMimetype);
	}

	/*
	 * (non-Javadoc)
	 * @see org.alfresco.repo.content.transform.AbstractContentTransformer2#transformInternal(org.alfresco.service.cmr.repository.ContentReader, org.alfresco.service.cmr.repository.ContentWriter, org.alfresco.service.cmr.repository.TransformationOptions)
	 */
	@Override
	protected void transformInternal(ContentReader reader,
			ContentWriter writer, TransformationOptions options)
			throws Exception {
		String sourceMimetype = reader.getMimetype();
		String targetMimetype = writer.getMimetype();
		if (!StringUtils.equals(MIMETYPE_PNG, targetMimetype)) {
			throw new IllegalArgumentException("Transformer can only convert from/to [" + MIMETYPE_SKETCHUP + "] to [" + MIMETYPE_PNG + "]");
		}

		// Write out the PNG
		InputStream in = reader.getContentInputStream();
		OutputStream out = writer.getContentOutputStream();
		try {
			BitmapExtractor extractor = extractors.get(sourceMimetype);
			if (extractor == null) {
				throw new IllegalArgumentException("No extractor configured " +
						"for source mimetype [" + sourceMimetype + "]");
			}
			extractor.extractBitmap(index, in, out);
		}
		finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}

	}

	/**
	 * A map of extractors keyed by mimetype.
	 * @param extractors
	 */
	public void setExtractors(Map<String, BitmapExtractor> extractors ) {
		this.extractors = extractors;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
