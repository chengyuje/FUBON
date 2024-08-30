package com.systex.jbranch.office.word.docx;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.SAXReader;

import com.systex.jbranch.office.word.ParseReader;
import com.systex.jbranch.office.word.WordBuilder;

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.DefaultArchiveDetector;
import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;
import de.schlichtherle.io.archive.zip.ZipDriver;

public class DocxParseReader extends ParseReader {

	private static final String WORD_DOCUMENT_XML = "/word/document.xml";
	private Reader reader = null;
	
	public DocxParseReader(String path) throws Exception {
		super(path);
	}

	@Override
	protected Document initDoc(String srcPath) throws Exception {

		DefaultArchiveDetector defaultArchiveDetector = new DefaultArchiveDetector(ArchiveDetector.NULL, new Object[]{
                "docx", new ZipDriver()
        });

		File docXml = new File(srcPath + WORD_DOCUMENT_XML, defaultArchiveDetector);

		reader = new InputStreamReader(new FileInputStream(docXml), XML_ENCODING);
		doc = new SAXReader().read(reader);
		return doc;
	}

	@Override
	protected String getBookmarkTag() {

		return "//w:bookmarkStart";
	}

	@Override
	protected boolean isBookmarkEnd(String bookmarkStartId, Element element) {
		
		String bookmarkEndId = element.attributeValue("id");
		String tagName = element.getName();
		return "bookmarkEnd".equals(tagName) && bookmarkStartId.equals(bookmarkEndId);
	}
	
	@Override
	protected boolean isBookmarTag(Element element) {
		String tagName = element.getName();
		return "bookmarkStart".equals(tagName)
				|| "bookmarkEnd".equals(tagName);
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.ParseReader#save(java.lang.String)
	 */
	@Override
	public void save(String savePath) throws Exception {

		if(!srcPath.equals(savePath)){
			FileUtils.copyFile(new File(srcPath), new File(savePath), false);
		}
		
		DefaultArchiveDetector defaultArchiveDetector = new DefaultArchiveDetector(ArchiveDetector.NULL, new Object[]{
                "docx", new ZipDriver()
        });

		File docXml = new File(savePath + WORD_DOCUMENT_XML, defaultArchiveDetector);
		
		Writer writer = new OutputStreamWriter(new FileOutputStream(docXml), XML_ENCODING);
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(new DOMWriter().write(doc)),
                new StreamResult(writer));
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.WordReader#getBuilder()
	 */
	@Override
	public WordBuilder getBuilder() {

		return new DocxParseBuilder(this);
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.WordReader#getTotalPageSize()
	 */
	@Override
	public int getTotalPageSize() {
		throw new UnsupportedOperationException("未支援的操作");
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.WordReader#close()
	 */
	@Override
	public void close() throws Exception {
		if(reader != null){
			reader.close();
		}
	}
}
