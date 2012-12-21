package com.jaromin.alfresco.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.jaromin.alfresco.extractor.PngExtractor;

public class PngExtractorTest {

	PngExtractor extractor;
	
	@Before
	public void setUp() {
		extractor = new PngExtractor();
	}
	
	@Test
	public void testCountBitmaps() {
		Map<String, Integer> skps = new HashMap<String,Integer>();
		skps.put("fireplace.skp", 6);
		skps.put("puppet theater.skp", 399);
		skps.put("puppet theater2.skp", 8);
		skps.put("x-men bed.skp", 14);

		try {
			for (Entry<String,Integer> e : skps.entrySet()) {
				File f = new File("../src/test/resources/sketchup/" + e.getKey());
				InputStream in = new FileInputStream(f);
				int count = extractor.countBitmaps(in);
				assertEquals("File [" + e.getKey() + "]", e.getValue().intValue(), count);
				IOUtils.closeQuietly(in);
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testExtractBitmap() {
		File outputDir = new File("target/" + this.getClass().getSimpleName());
		outputDir.mkdirs();
		
		try {
			File skp = new File("../src/test/resources/sketchup/x-men bed.skp");
			int count = extractor.countBitmaps(skp);

			for (int i = 0; i < count; i++) {
				int n = i+1;
				InputStream in = new FileInputStream(skp);
				OutputStream out = new FileOutputStream(new File(outputDir, "extracted." + n + ".png"));
				boolean success = extractor.extractBitmap(n, in, out);
				assertTrue(success);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
