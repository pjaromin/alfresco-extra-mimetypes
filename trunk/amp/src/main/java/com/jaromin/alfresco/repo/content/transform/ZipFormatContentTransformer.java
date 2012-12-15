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
