package com.systex.jbranch.office.word.wml;

import java.io.FileInputStream;
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
import com.systex.jbranch.platform.common.errHandle.JBranchException;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileOutputStream;

public class WmlParseReader extends ParseReader {

	private Reader reader = null;
	
	public WmlParseReader(String path) throws Exception {
		super(path);
	}

	@Override
	protected Document initDoc(String path) throws Exception {
		
		File xml = new File(path);
		reader = new InputStreamReader(new FileInputStream(xml), XML_ENCODING);
		doc = new SAXReader().read(reader);
		return doc;
	}

	@Override
	protected String getBookmarkTag() {

		return "//aml:annotation[@w:type='Word.Bookmark.Start']";
	}

	@Override
	protected boolean isBookmarkEnd(String bookmarkStartId, Element element) {
		String bookmarkEndId = element.attributeValue("id");
		String type = element.attributeValue("type");
		String tagName = element.getName();

		return "annotation".equals(tagName) && "Word.Bookmark.End".equals(type)
				&& bookmarkStartId.equals(bookmarkEndId);
	}

	@Override
	protected boolean isBookmarTag(Element element) {
		String type = element.attributeValue("type");
		return "Word.Bookmark.Start".equals(type)
				|| "Word.Bookmark.End".equals(type);
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.ParseReader#save(java.lang.String)
	 */
	@Override
	public void save(String savePath) throws Exception {
		if(!srcPath.equals(savePath)){
			FileUtils.copyFile(new File(srcPath), new File(savePath), false);
		}
		Writer writer = new OutputStreamWriter(new FileOutputStream(savePath), XML_ENCODING);
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(new DOMWriter().write(doc)),
                new StreamResult(writer));
        writer.close();
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.WordReader#getBuilder()
	 */
	@Override
	public WordBuilder getBuilder() {

		return new WmlParseBuilder(this);
	}
	
	public int getTotalPageSize(){
		
		Element ele = (Element) doc.selectSingleNode("/w:wordDocument/o:DocumentProperties/o:Pages");
		return Integer.parseInt(ele.getText()); 
	}

	@Override
	public void close() throws Exception {
		if(reader != null){
			reader.close();
		}
	}
}
