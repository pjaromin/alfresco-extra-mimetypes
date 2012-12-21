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

import java.io.IOException;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.alfresco.repo.content.transform.AbstractContentTransformer2;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author pjaromin
 *
 */
public class ZipFormatContentTransformer extends AbstractContentTransformer2 {

	/*
	 * (non-Javadoc)
	 * @see org.alfresco.repo.content.transform.AbstractContentTransformerLimits#isTransformable(java.lang.String, java.lang.String, org.alfresco.service.cmr.repository.TransformationOptions)
	 */
	@Override
	public boolean isTransformable(String sourceMimetype,
			String targetMimetype, TransformationOptions options) {
		// Best we can really do at this point is check if the source is a ZIP.
		// Narrow down in a sub-class if necessary.
		return "application/zip".equals(sourceMimetype)
				|| "application/x-zip-compressed".equals(sourceMimetype);
	}

	private String zipEntryPath;
	
	@Override
	protected void transformInternal(ContentReader reader,
			ContentWriter writer, TransformationOptions options)
			throws Exception {

		ZipInputStream zip = new ZipInputStream(reader.getContentInputStream());
		OutputStream out = writer.getContentOutputStream();
		try {
			extractEntry(this.getZipEntryPath(), zip, out);
		} 
		finally {
			// Close the streams...
			IOUtils.closeQuietly(zip);
			IOUtils.closeQuietly(out);
		}
		
	}

	/**
	 * 
	 * @param path
	 * @param zip
	 * @param out
	 * @throws IOException
	 */
	protected void extractEntry(String path, ZipInputStream zip, OutputStream out)
			throws IOException {
		
		// Use as regex for more flexibility...
		Pattern p = Pattern.compile(path);

		ZipEntry entry = null;
		while ((entry = zip.getNextEntry()) != null) {
			Matcher m = p.matcher(entry.getName());
			if (m.matches()) {
				IOUtils.copy(zip, out);
				zip.closeEntry();
				break;
			}
		}
	}

	public String getZipEntryPath() {
		return this.zipEntryPath;
	}
	
	public void setZipEntryPath(String zipEntryPath) {
		this.zipEntryPath = zipEntryPath;
	}
	
	protected Log log() {
		return LogFactory.getLog(getClass());
	}
}
