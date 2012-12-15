package com.jaromin.alfresco.repo.content.transform;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.TransformationOptions;
import org.alfresco.util.TempFileProvider;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.tika.io.IOUtils;

/**
 * Content transformer for CorelDraw X4+ files that converts them to
 * BMP thumbnails or PDF.
 * @author pjaromin
 *
 */
public class CorelDrawContentTransformer extends ZipFormatContentTransformer {

	private static final String MIMETYPE_CDR = "application/vnd.corel-draw";
	
	private static final String MIMETYPE_BMP = "image/bmp";
	
	private static final String MIMETYPE_PDF = "application/pdf";

	@Override
	public boolean isTransformableMimetype(String sourceMimetype,
			String targetMimetype, TransformationOptions options) {
		return MIMETYPE_CDR.equals(sourceMimetype) 
				&& (MIMETYPE_BMP.equals(targetMimetype) 
						|| MIMETYPE_PDF.equals(targetMimetype));
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
		log().debug("Asked to tranform from [" + sourceMimetype + "] to [" + targetMimetype + "]");
		if (MIMETYPE_BMP.equals(targetMimetype)) {
			super.transformInternal(reader, writer, options);
		}
		else if (MIMETYPE_PDF.equals(targetMimetype)) {
			InputStream in = reader.getContentInputStream();
			OutputStream out = writer.getContentOutputStream();
			try {
				ZipInputStream zip = new ZipInputStream(in);
				writePDF(zip, out);
			}
			finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		}
		else {
//			throw new IllegalArgumentException("Unable to transform from ["
//					+ sourceMimetype + "] to [" + targetMimetype + "]");
		}
	}

	/**
	 * Writes the page thumbnails from the CDR ZIP file to a PDF
	 * @param zip
	 * @param writer
	 * @throws IOException 
	 * @throws ZipException 
	 * @throws COSVisitorException 
	 */
	protected void writePDF(ZipInputStream zip, OutputStream out) 
			throws ZipException, IOException, COSVisitorException  {
		// TODO Assumes the pages come in sequence. Probably should
		// explicitly sort this somehow.
		Map<File, Dimension> pageImages = new TreeMap<File, Dimension>();
		Pattern p = Pattern.compile("metadata/thumbnails/(page\\d+).bmp");
		FileImageOutputStream imageOut = null;
		try {
			File dir = TempFileProvider.getTempDir();
			ZipEntry entry = null;
			while ((entry = zip.getNextEntry()) != null) {
				Matcher m = p.matcher(entry.getName());
				if (m.matches()){
					String filename = m.group(1);
					File pageBitmap = new File(dir, filename + ".jpg");
					BufferedImage image = ImageIO.read(zip);
					zip.closeEntry();

					pageImages.put(pageBitmap, new Dimension(image.getWidth(), image.getHeight()));
					Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
					ImageWriter imageWriter = (ImageWriter) iter.next();
					ImageWriteParam iwp = imageWriter.getDefaultWriteParam();
					iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					iwp.setCompressionQuality(1);

					imageOut = new FileImageOutputStream(pageBitmap);
					imageWriter.setOutput(imageOut);
					IIOImage iioImage = new IIOImage(image, null, null);
					imageWriter.write(null, iioImage, iwp);
					imageWriter.dispose();
				}
			}
			buildPdfFromImages(pageImages, out);
		}
		finally {
			if (imageOut != null) {
				try {imageOut.close();} catch (Exception e) {}
			}
			
		}
		
	}

	/**
	 * 
	 * @param pageImages
	 * @param out
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws COSVisitorException
	 */
	private void buildPdfFromImages(Map<File, Dimension> pageImages, OutputStream out)
			throws IOException, FileNotFoundException, COSVisitorException {
		PDDocument doc = new PDDocument();			
		for (Map.Entry<File, Dimension> entry : pageImages.entrySet()) {
			File pFile = entry.getKey();
			Dimension d = entry.getValue();
			PDRectangle size = new PDRectangle(d.width, d.height);
			PDPage page = new PDPage(size);
			doc.addPage(page);

			PDXObjectImage ximage = new PDJpeg(doc, new FileInputStream(pFile));
			PDPageContentStream contentStream = new PDPageContentStream(doc, page);
			contentStream.drawImage(ximage, size.getLowerLeftX(), size.getLowerLeftY());
	        contentStream.close();				
		}
		doc.save(out);
	}

	/*
	 * (non-Javadoc)
	 * @see com.jaromin.alfresco.repo.content.transform.ZipFormatContentTransformer#getZipEntryPath()
	 */
	@Override
	public String getZipEntryPath() {
		return "metadata/thumbnails/thumbnail.bmp";
	}

}
