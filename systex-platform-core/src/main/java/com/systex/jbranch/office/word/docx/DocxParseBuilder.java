package com.systex.jbranch.office.word.docx;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;

import com.systex.jbranch.office.word.ParseBuilder;
import com.systex.jbranch.office.word.WordReader;

public class DocxParseBuilder extends ParseBuilder {

	public DocxParseBuilder(WordReader reader) {
		super(reader);
	}

	@Override
	protected String getRFontsAttrName2() {

		return "eastAsia";
	}

	@Override
	protected String getRFontsAttrName3() {

		return "hAnsi";
	}
	
	@Override
	protected Element getWXFont(String font) {

		return null;
	}

	@Override
	protected String getSzCsTagName() {

		return "szCs";
	}

	@Override
	protected Namespace getWspNS() {

		return null;
	}

	@Override
	public void insertPageNumber(Element owner, int index, int start, int total) {
		throw new UnsupportedOperationException("docx尚未支援insertPageNumber");
	}

}
