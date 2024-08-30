package com.systex.jbranch.office.word.bookmark;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.office.word.WordBuilder;

/**
 * @author Angus
 * 供操作書籤使用
 */
public class BookmarkContentXmlParseImpl implements BookmarkContent<Element>{
	
	private static String charset = "MS950";	//需與文件編碼一致
	private WordBuilder builder;
	private Element bookmarkStart;
	private List<Element> elements = new ArrayList<Element>();
	private Logger logger = LoggerFactory.getLogger(BookmarkContentXmlParseImpl.class);
	
	public BookmarkContentXmlParseImpl(WordBuilder builder, Element bookmarkStart){
		this.builder = builder;
		this.bookmarkStart = bookmarkStart;
	}
	
	/**
	 * 指定文件編碼
	 * @param charset
	 */
	public static void setDocumentCharset(String c){
		charset = c;
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.BookmarkContent#appendContent(java.lang.Object)
	 */
	public void appendContent(Element element){
		elements.add(element);
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.bookmark.BookmarkContent#setText(java.lang.String)
	 */
	public void setText(String value) throws Exception {
		int textLength = getWrTextLength();

		if(textLength > 0){
			value = getProcessText(value, textLength);
			builder.setText(elements.get(0), value);
			for (int i = 1; i < elements.size(); i++) {
				builder.setText(elements.get(i), "");
			}
			return;
		}
//		if(elements.size() > 0){
//			clearContent();
//		}
		Element owner = bookmarkStart.getParent();
		int index = owner.indexOf(bookmarkStart) + 1;
		builder.insertText(bookmarkStart.getParent(), index, value);
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.bookmark.BookmarkContent#setFuncationVarable(java.lang.String)
	 */
	public void setFuncationVarable(String value) throws Exception {
		
		int textLength = getWrTextLength();
		if(textLength > 0){
			for (int i = 0; i < elements.size(); i++) {
				builder.setText(elements.get(i), "");
			}
		}

		Element owner = bookmarkStart.getParent();
		int index = owner.indexOf(bookmarkStart) + 1;
		builder.insertFuncationVarable(bookmarkStart.getParent(), index, value);
	}
	
//	/* (non-Javadoc)
//	 * @see com.systex.jbranch.office.word.bookmark.BookmarkContent#setText(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
//	 */
//	public void setText(String value, String font, String color, String size) throws Exception{
//		
//		int textLength = getWrTextLength();
//
//		if(textLength > 0){
//			value = getProcessText(value, textLength);
//			builder.setText(elements.get(0), value);
//			for (int i = 1; i < elements.size(); i++) {
//				builder.setText(elements.get(i), "");
//			}
//			return;
//		}
//		if(elements.size() > 0){
//			clearContent();
//		}
//        Element owner = bookmarkStart.getParent();
//        int index = owner.indexOf(bookmarkStart) + 1;
//		builder.insertText(bookmarkStart.getParent(), index, value, font, color, size);
//	}

	private int getWrTextLength() throws UnsupportedEncodingException {
		int textLength = 0;
		for (int i = 0; i < elements.size(); i++) {
			Element wt = (Element) elements.get(i).selectSingleNode("w:t");
			if(wt != null){
				textLength += wt.getText().getBytes(charset).length;
			}
		}
		return textLength;
	}
	
	private int getInstrTextLength() throws UnsupportedEncodingException {
		int textLength = 0;
		for (int i = 0; i < elements.size(); i++) {
			Element wt = (Element) elements.get(i).selectSingleNode("w:instrText");
			if(wt != null){
				textLength += wt.getText().getBytes(charset).length;
			}
		}
		return textLength;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer  sb = new StringBuffer();
		if(elements != null){
			for (Element element : elements) {
				Element wt = (Element) element.selectSingleNode("w:t");
				if(wt != null){
					sb.append(wt.getText());
				}
			}
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.BookmarkContent#getText()
	 */
	public String getText() {

		return toString();
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.bookmark.BookmarkContent#getFuncationVarable()
	 */
	public String getFuncationVarable() {

		return null;
	}

//	/* (non-Javadoc)
//	 * @see com.systex.jbranch.office.word.bookmark.BookmarkContent#insertFuncationVarable(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
//	 */
//	public void setFuncationVarable(String value, String font, String color,
//			String size) throws Exception{
//		int textLength = 0;
//		for (int i = 0; i < elements.size(); i++) {
//			Element instrText = (Element) elements.get(i).selectSingleNode("w:instrText");
//			if(instrText != null){
//				textLength += instrText.getText().getBytes(charset).length;
//			}
//		}
//		
//		if(textLength > 0){
////			value = getProcessText(value, textLength);
//			builder.setFuncationVarable(elements.get(0), value);
//			for (int i = 1; i < elements.size(); i++) {
//				builder.setFuncationVarable(elements.get(i), "");
//			}
//		}
//		if(elements.size() > 0){
//			clearContent();
//		}
//		Element owner = bookmarkStart.getParent();
//		int index = owner.indexOf(bookmarkStart) + 1;
//		builder.insertFuncationVarable(owner, index, value, font, color, size);
//	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.bookmark.BookmarkContent#clearContent()
	 */
	public void clearContent() {
		
		for (Element element : elements) {
			Element owner = element.getParent();
			owner.remove(element);
		}
	}
	
	private String getProcessText(String content, int limitLength) throws UnsupportedEncodingException {

		int textLength = content.getBytes(charset).length;

		if(textLength > limitLength){
			byte[] bytes = content.getBytes(charset);
			content = new String(bytes, 0, limitLength, charset);
		}else if(textLength < limitLength){
			content = StringUtils.rightPad(content, limitLength);
		}

		return content;
	}

	@Override
	public void setPageCode(int startPage, int totalPage) throws Exception{
		logger.debug("angus test startPage=" + startPage + ", totalPage=" + totalPage);
		Element owner = bookmarkStart.getParent();
		int index = owner.indexOf(bookmarkStart) + 1;
		builder.insertPageNumber(bookmarkStart.getParent(), index, startPage, totalPage);
	}

}	