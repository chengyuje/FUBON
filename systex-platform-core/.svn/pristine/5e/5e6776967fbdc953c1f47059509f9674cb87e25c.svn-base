package com.systex.jbranch.office.word;

import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public abstract class ParseBuilder extends WordBuilder {

	public ParseBuilder(WordReader reader) {
		super(reader);
	}
	
	@Override
	public void insertFuncationVarable(Element owner, int index, String value) {
		
		Namespace ns = getWNs();
		Namespace wspNs = getWspNS();
		
		Element wRpr = (Element) owner.selectSingleNode("w:pPr/w:rPr");
		Element wRpr1 = null;
		Element wRpr2 = null;
		Element wRpr3 = null;

		if(wRpr != null){
			wRpr1 = (Element) wRpr.clone();
			wRpr2 = (Element) wRpr.clone();
			wRpr3 = (Element) wRpr.clone();
		}

		Element wr1 = getWr(ns, wspNs, wRpr1);
		Element fldChar1 = getfldChar(ns, "begin");
		wr1.add(fldChar1);
		
//		Element wr2 = getWr(ns, font, color, size);
//		Element instrText1 = getInstrText(ns, "preserve", null);
//		wr2.add(instrText1);
		
		Element wr3 = getWr(ns, wspNs, wRpr2);
		Element instrText2 = getInstrText(ns, null, value);
		wr3.add(instrText2);
		
//		Element wr4 = getWr(ns, font, color, size);
//		Element instrText3 = getInstrText(ns, "preserve", null);
//		wr4.add(instrText3);

		Element wr5 = getWr(ns, wspNs, wRpr3);
		Element fldChar2 = getfldChar(ns, "end");
		wr5.add(fldChar2);
		
		List content = owner.content();
		content.add(index, wr5);
//		content.add(index, wr4);
		content.add(index, wr3);
//		content.add(index, wr2);
		content.add(index, wr1);
	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public void insertFuncationVarable(Element owner, int index,
//			String value, String font, String color, String size){
//		Namespace ns = getWrNs(owner);
//		Namespace wspNs = getWspNS(owner);
//		
//		Element wr1 = getWr(ns, wspNs, font, color, size);
//		Element fldChar1 = getfldChar(ns, "begin");
//		wr1.add(fldChar1);
//		
////		Element wr2 = getWr(ns, font, color, size);
////		Element instrText1 = getInstrText(ns, "preserve", null);
////		wr2.add(instrText1);
//		
//		Element wr3 = getWr(ns, wspNs, font, color, size);
//		Element instrText2 = getInstrText(ns, null, value);
//		wr3.add(instrText2);
//		
////		Element wr4 = getWr(ns, font, color, size);
////		Element instrText3 = getInstrText(ns, "preserve", null);
////		wr4.add(instrText3);
//
//		Element wr5 = getWr(ns, wspNs, font, color, size);
//		Element fldChar2 = getfldChar(ns, "end");
//		wr5.add(fldChar2);
//		
//		List content = owner.content();
//		content.add(index, wr5);
////		content.add(index, wr4);
//		content.add(index, wr3);
////		content.add(index, wr2);
//		content.add(index, wr1);
//	}
	
	protected abstract Namespace getWspNS();

	@Override
	public void setFuncationVarable(Element element, String value){
		Element wr = (Element) element.selectSingleNode("w:instrText");
		if(wr != null){
			wr.setText(value);
		}
	}
	
	private Element getWr(boolean hasAttr, Namespace ns, Namespace wspNs, Element rPr){

		Element wr = DocumentHelper.createElement(new QName("r", ns));
		if(wspNs == null){
			wspNs = ns;
		}
		if(hasAttr){
			wr.addAttribute(new QName("rsidR", wspNs), "00F54A11");
		}
		if(rPr != null){
			wr.add(rPr);
		}
		return wr;
	}
	
//	private Element getWr(boolean hasAttr, Namespace ns, Namespace wspNs, String font, String color, String size){
//
//		Element wr = DocumentHelper.createElement(new QName("r", ns));
//		if(wspNs == null){
//			wspNs = ns;
//		}
//		if(hasAttr){
//			wr.addAttribute(new QName("rsidR", wspNs), "00F54A11");
//		}
//    	wr.add(createRPr(ns, font, color, size));
//		return wr;
//	}
	
	private Element getWr(Namespace ns, Namespace wspNs, Element rPr){

		return getWr(true, ns, wspNs, rPr);
	}
//	
//	private Element getWr(Namespace ns, Namespace wspNs, String font, String color, String size){
//
//		return getWr(true, ns, wspNs, font, color, size);
//	}
//
//	private Element createRPr(Namespace ns, String font, String color, String size){
//		Element rPr = DocumentHelper.createElement(new QName("rPr", ns));
//    	
//    	Element rFonts = DocumentHelper.createElement(new QName("rFonts", ns));
//    	rFonts.addAttribute(new QName("ascii", ns), font);
//    	rFonts.addAttribute(new QName(this.getRFontsAttrName2(), ns), font);
//    	rFonts.addAttribute(new QName(this.getRFontsAttrName3(), ns), font);
//    	
//    	Element wxFont = getWXFont(font);
//    	
//    	Element eColor = DocumentHelper.createElement(new QName("color", ns));
//    	eColor.addAttribute(new QName("val", ns), color);
//    	
//    	Element sz = DocumentHelper.createElement(new QName("sz", ns));
//    	sz.addAttribute(new QName("val", ns), size);
//    	
//    	Element szCs = DocumentHelper.createElement(new QName(this.getSzCsTagName(), ns));
//    	szCs.addAttribute(new QName("val", ns), size);
//    	
//    	rPr.add(rFonts);
//    	if(wxFont != null){
//    		rPr.add(wxFont);
//    	}
//    	rPr.add(eColor);
//    	rPr.add(sz);
//    	rPr.add(szCs);
//    	return rPr;
//	}
	
	private Element getfldChar(Namespace ns, String fldCharType){
		Element fldChar = DocumentHelper.createElement(new QName("fldChar", ns));
		fldChar.addAttribute(new QName("fldCharType", ns), fldCharType);
		return fldChar;
	}
	
	private Element getInstrText(Namespace ns, String attrValue, String value){
		Element instrText = DocumentHelper.createElement(new QName("instrText", ns));
		Namespace xmlNs = instrText.getNamespaceForPrefix("xml");
		if(attrValue != null){
			instrText.addAttribute(new QName("space", xmlNs), attrValue);
		}
		if(value != null){
			instrText.setText(value);
		}
		return instrText;
	}
	
	protected abstract String getRFontsAttrName2();
	
	protected abstract String getRFontsAttrName3();
	
	protected abstract Element getWXFont(String font);
	
	protected abstract String getSzCsTagName();
	
	@Override
	public void insertText(Element owner, int index, String value) {
		Element wRpr = (Element) owner.selectSingleNode("w:pPr/w:rPr");
		Element newWRpr = null;
		Namespace ns = getWNs();
		Element wr = DocumentHelper.createElement(new QName("r", ns));;
		owner.content().add(index, wr);
		if(wRpr != null){
			newWRpr = (Element) wRpr.clone();
		}
		if(newWRpr != null){
			wr.add(newWRpr);
		}
		
		Element wt = wr.addElement("w:t");
		wt.setText(value);
	}

//	@Override
//	public void insertText(Element owner, int index, String value, String font, String color, String size){
//		Namespace ns = getWrNs(owner);
//		Namespace wspNs = getWspNS(owner);
//		Element wr = getWr(false, ns, wspNs, font, color, size);
//		Element wt = DocumentHelper.createElement(new QName("t", ns));
//        wt.setText(value);
//        wr.add(wt);
//		owner.content().add(index, wr);
//	}

	private Namespace getWNs() {
		Namespace ns = reader.getDocument().getRootElement().getNamespaceForPrefix("w");
		return ns;
	}
	
	@Override
	public void setText(Element element, String value){
		Element wr = (Element) element.selectSingleNode("w:t");
		if(wr != null){
			wr.setText(value);
		}
	}
}
