package com.jaromin.alfresco.repo.content.transform;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class ZipFormatContentTransformerTest {

	ZipFormatContentTransformer transformer;
	
	@Before
	public void setUp() throws Exception {
		transformer = new ZipFormatContentTransformer(); 
	}

	@Test
	public void testExtractEntry() {

		File outputDir = new File("target/" + this.getClass().getSimpleName());
		outputDir.mkdirs();

		Map<String, String> entries = new HashMap<String,String>();
		entries.put("Rosette.cdr","metadata/thumbnails/thumbnail.bmp");
		entries.put("3Pages.cdr","metadata/thumbnails/thumbnail.bmp");
		entries.put("business-card.myt", "sample.pdf");
		entries.put("email.zip","._preview.pdf");

		try {
			for (Map.Entry<String,String> entry : entries.entrySet()) {
				ZipInputStream zip = new ZipInputStream(new FileInputStream("src/test/resources/zip/" + entry.getKey()));
				String ext = FilenameUtils.getExtension(entry.getValue());
				FileOutputStream out = new FileOutputStream(new File(outputDir,entry.getKey() + "." + ext));
				transformer.extractEntry(entry.getValue(), zip, out);
				IOUtils.closeQuietly(zip);
				IOUtils.closeQuietly(out);
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
