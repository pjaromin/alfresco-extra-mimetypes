package com.jaromin.alfresco.repo.content.transform;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipInputStream;

import org.apache.tika.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import ch.randelshofer.media.riff.RIFFChunk;
import ch.randelshofer.media.riff.RIFFParser;
import ch.randelshofer.media.riff.RIFFVisitor;
import ch.randelshofer.util.AbortException;
import ch.randelshofer.util.ParseException;

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

	
	@Test
	public void parsePreX4Test() {
		try {
			final RIFFParser parser = new RIFFParser();
			InputStream in = new FileInputStream(new File("src/test/resources/coreldraw/preX4/Electrical.cdr"));
			RIFFVisitor rv = new RIFFVisitor() {
	
				@Override
				public void enterGroup(RIFFChunk group) throws ParseException,
						AbortException {
					System.out.println("enterGroup [" + group + "], " + group.getID() + ":" + group.getParserMessage());
				}
	
				@Override
				public void leaveGroup(RIFFChunk group) throws ParseException,
						AbortException {
					System.out.println("leaveGroup [" + group + "]");
				}
	
				@Override
				public void visitChunk(RIFFChunk group, RIFFChunk chunk)
						throws ParseException, AbortException {
					System.out.println("visitChunk [" + group + ", " + chunk + "]");

					System.out.println(parser.idToString(group.getType()));
					try {
						System.out.println(chunk.getSize() + " == " + chunk.getData());
						OutputStream out = new FileOutputStream("target/" + chunk.toString() + ".bin");
						out.write(chunk.getData());
						IOUtils.closeQuietly(out);
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Enumeration e = chunk.propertyChunks();
					while (e.hasMoreElements()) {
						RIFFChunk c = (RIFFChunk)e.nextElement();
						System.out.println("PROPERTY CHUNK: " + c);
						
					}
				}
				
			};
	
			parser.parse(in, rv);
		}
		catch (Exception e) {
			//e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
