package com.systex.jbranch.office.word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.systex.jbranch.office.word.bookmark.BookmarkContent;
import com.systex.jbranch.office.word.bookmark.BookmarkContentXmlParseImpl;

public abstract class ParseReader extends WordReader {

	public ParseReader(String path) throws Exception {
		super(path);
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.WordReader#initDoc(java.lang.String)
	 */
	@Override
	protected abstract Document initDoc(String path) throws Exception;
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.WordReader#getBookmarkRange()
	 */
	@Override
	public Map<String, BookmarkContent> getBookmarkRange() {
		WordBuilder builder = getBuilder();
		String bookmarkStartTag = getBookmarkTag();
		List<Element> bookmarkStartList = doc.selectNodes(bookmarkStartTag);
		HashMap<String, BookmarkContent> ranges = new HashMap<String, BookmarkContent>();
		for (Element element : bookmarkStartList) {
			String bmId = element.attributeValue("id");
			String bmName = element.attributeValue("name");
			BookmarkContent bmContent = new BookmarkContentXmlParseImpl(builder, element);
			ranges.put(bmName, bmContent);
			appendRange(bmId, element, bmContent);
		}
		return ranges;
	}
	
	protected void appendRange(String bmId, Element element,
			BookmarkContent bmContent) {
		Element nextElement = (Element) element
				.selectSingleNode("following-sibling::*");
		if (nextElement != null && !isBookmarkEnd(bmId, nextElement)) {
			if (!isBookmarTag(nextElement)) {
				bmContent.appendContent(nextElement);
			}
			appendRange(bmId, nextElement, bmContent);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.office.word.WordReader#save(java.lang.String)
	 */
	@Override
	public abstract void save(String savePath) throws Exception;
	
	protected abstract String getBookmarkTag();

	protected abstract boolean isBookmarkEnd(String bookmarkStartId, Element element);
	
	protected abstract boolean isBookmarTag(Element element);
}
