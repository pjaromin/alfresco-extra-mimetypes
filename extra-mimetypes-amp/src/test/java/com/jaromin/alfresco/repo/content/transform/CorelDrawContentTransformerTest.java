package com.jaromin.alfresco.repo.content.transform;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import org.apache.tika.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

public class CorelDrawContentTransformerTest {

	CorelDrawContentTransformer transformer;
	
	@Before
	public void setUp() throws Exception {
		transformer = new CorelDrawContentTransformer();
	}

	@Test
	public void writePDFTest() {

		File outputDir = new File("target/" + this.getClass().getSimpleName());
		outputDir.mkdirs();
		
		String[] cdrs = new String[]{"Rosette.cdr","3Pages.cdr"};
		try {
			for (String cdr : cdrs) {
				InputStream in = new FileInputStream(new File("src/test/resources/coreldraw/" + cdr));
				OutputStream pdf = new FileOutputStream(new File(outputDir, cdr + ".pdf"));
				ZipInputStream zip = new ZipInputStream(in);
				transformer.writePDF(zip, pdf);
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(pdf);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
