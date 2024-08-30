package com.systex.jbranch.office.word;

import org.dom4j.Element;
import org.dom4j.Node;

public abstract class WordBuilder {
	
	protected WordReader reader;

	public WordBuilder(WordReader reader){
		this.reader = reader;
	}

	public abstract void insertFuncationVarable(Element owner, int index, String value);
	
//	public abstract void insertFuncationVarable(Element owner, int index, String value, 
//			String font, String color, String size);
	
	public abstract void setFuncationVarable(Element element, String value);
	
	public abstract void insertText(Element owner, int index, String value);
	
//	public abstract void insertText(Element owner, int index, String value, String font, String color, String size);
	
	public abstract void setText(Element element, String value);
	
	public abstract void insertPageNumber(Element owner, int index, int start, int total) throws Exception;
}
