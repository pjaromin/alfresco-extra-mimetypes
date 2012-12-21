package com.jaromin.alfresco.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class WindowsBmpExtractorTest {

	BitmapExtractor extractor;
	
	@Before
	public void setUp() {
		extractor = new WindowsBmpExtractor();
	}

	@Test
	public void testCountBitmapsFile() {
		Map<String, Integer> bmps = new HashMap<String,Integer>();
//		bmps.put("Snapshot.bmp", 1);
//		bmps.put("coreldraw/preX4/business card.cdr", 24);

		try {
			for (Entry<String,Integer> e : bmps.entrySet()) {
				File f = new File("../src/test/resources/" + e.getKey());
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
		
//		try {
//			File file = new File("../src/test/resources/coreldraw/preX4/business card.cdr");
//			FileInputStream in = new FileInputStream(file);
//			int count = extractor.countBitmaps(in);

//			for (int i = 0; i < count; i++) {
//				int n = i+1;
//				in = new FileInputStream(file);
//				OutputStream out = new FileOutputStream(new File(outputDir, "extracted." + n + ".png"));
//				boolean success = extractor.extractBitmap(n, in, out);
//				assertTrue(success);
//			}
//		} 
//		catch (IOException e) {
//			e.printStackTrace();
//			fail(e.getMessage());
//		}
	}
}
