package com.systex.jbranch.office.word.wml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.office.word.ParseBuilder;
import com.systex.jbranch.office.word.WordReader;

public class WmlParseBuilder extends ParseBuilder {

	private Logger logger = LoggerFactory.getLogger(WmlParseBuilder.class);
	
	public WmlParseBuilder(WordReader reader) {
		super(reader);
	}

	@Override
	protected String getRFontsAttrName2() {

		return "fareast";
	}

	@Override
	protected String getRFontsAttrName3() {

		return "h-ansi";
	}
	
	@Override
	protected Element getWXFont(String font) {
		Namespace ns = getWXNS();
		Element wr = DocumentHelper.createElement(new QName("font", ns));
		wr.addAttribute(new QName("val", ns), font);
		return wr;
	}

	@Override
	protected String getSzCsTagName() {

		return "sz-cs";
	}

	@Override
	public void insertPageNumber(Element owner, int index, int start, int total) throws Exception {
		
		Element fldSimple = getFldSimple(start);
		
		Namespace wNs = getWNS();
		Element r = DocumentHelper.createElement(new QName("r", wNs));
		Element rPr = DocumentHelper.createElement(new QName("rPr", wNs));
		Element rFonts = DocumentHelper.createElement(new QName("rFonts", wNs));
		Element t = DocumentHelper.createElement(new QName("t", wNs));
		r.add(rPr);
		rPr.add(rFonts);
		rFonts.addAttribute(new QName("hint", wNs), "fareast");
		r.add(t);
		t.setText("/" + total);
		
//		Element sectPr = (Element) reader.getDocument().selectSingleNode("/w:wordDocument/w:body/w:sectPr");
//		Element ftr = (Element) reader.getDocument().selectSingleNode("/w:wordDocument/w:body/w:ftr");
//		Element pict = this.getPict(String.valueOf(total));

//		if(ftr == null){
//			Element style1 = this.getStyleElement("a3", "header", "首頁", "a4", true);
//			Element style2 = this.getStyleElement("a4", "頁首 字元", null, "a3", false);
//			Element style3 = this.getStyleElement("a5", "footer", "首尾", "a6", true);
//			Element style4 = this.getStyleElement("a6", "首尾 字元", null, "a5", false);
//			
//			Element styles = (Element) reader.getDocument().selectSingleNode("/w:wordDocument/w:styles");
//			styles.add(style1);
//			styles.add(style2);
//			styles.add(style3);
//			styles.add(style4);
//
//			ftr = this.getFtrElement(pict);
//			sectPr.add(ftr);
//		}else{
//			ftr.add(pict);
//		}

		List content = owner.content();
		content.add(index, r);
		content.add(index, fldSimple);
		
		Element pgNumType = DocumentHelper.createElement(new QName("pgNumType", wNs));
		pgNumType.addAttribute(new QName("start", wNs), String.valueOf(start));
		
		Element pgMar = (Element) reader.getDocument().selectSingleNode("/w:wordDocument/w:body/w:sectPr/w:pgMar");
		Element parent = pgMar.getParent();
		index = parent.indexOf(pgMar);
		parent.content().add(index, pgNumType);
	}

	private Element getFldSimple(int start) {
		Namespace wNs = getWNS();
		Namespace wspNs = getWspNS();
		
		Element fldSimple = DocumentHelper.createElement(new QName("fldSimple", wNs));
		Element r = DocumentHelper.createElement(new QName("r", wNs));
		Element rPr = DocumentHelper.createElement(new QName("rPr", wNs));
		Element noProof = DocumentHelper.createElement(new QName("noProof", wNs));
		Element lang = DocumentHelper.createElement(new QName("lang", wNs));
		Element t = DocumentHelper.createElement(new QName("t", wNs));
		fldSimple.addAttribute(new QName("instr", wNs),  "PAGE   \\* MERGEFORMAT");
		fldSimple.add(r);
		r.addAttribute(new QName("rsidRPr", wspNs),  "00A128EC");
		r.add(rPr);
		rPr.add(noProof);
		rPr.add(lang);
		lang.addAttribute(new QName("val", wNs),  "ZH-TW");
		r.add(t);
		t.setText(String.valueOf(start));
		return fldSimple;
	}
	
	@Override
	protected Namespace getWspNS() {
		
		return reader.getDocument().getRootElement().getNamespaceForPrefix("wsp");
	}
	
	private Namespace getWNS(){
		return reader.getDocument().getRootElement().getNamespaceForPrefix("w");
	}
	
	private Namespace getWXNS(){
		return reader.getDocument().getRootElement().getNamespaceForPrefix("wx");
	}
	
	private Namespace getVNS(){
		return reader.getDocument().getRootElement().getNamespaceForPrefix("v");
	}
	
	private Namespace getONS(){
		return reader.getDocument().getRootElement().getNamespaceForPrefix("o");
	}
	
	private Element getStyleElement(String styleId, String nameVal, String uiNameVal, String linkVal, boolean isType1) throws FileNotFoundException, DocumentException{

		Namespace wNs = getWNS();
		Namespace wxNs = getWXNS();
		Element styleEle = DocumentHelper.createElement(new QName("style", wNs));
		Element nameEle = DocumentHelper.createElement(new QName("name", wNs));
		Element uiNameEle = null;
		if(isType1){
			 uiNameEle = DocumentHelper.createElement(new QName("uiName", wxNs));
		}
		
		Element basedOnEle = DocumentHelper.createElement(new QName("basedOn", wNs));
		Element linkEle = DocumentHelper.createElement(new QName("link", wNs));
		Element rsidEle = DocumentHelper.createElement(new QName("rsid", wNs));
		Element pPrEle = null;
		if(isType1){
			pPrEle = DocumentHelper.createElement(new QName("pPr", wNs));
		}
		
		Element rPrEle = DocumentHelper.createElement(new QName("rPr", wNs));
		styleEle.add(nameEle);
		if(isType1){
			styleEle.add(uiNameEle);
		}
		
		styleEle.add(basedOnEle);
		styleEle.add(linkEle);
		styleEle.add(rsidEle);
		if(isType1){
			styleEle.add(pPrEle);
		}
		
		styleEle.add(rPrEle);
		
		Element tabsEle = DocumentHelper.createElement(new QName("tabs", wNs));
		Element snapToGridEle = DocumentHelper.createElement(new QName("snapToGrid", wNs));
		if(isType1){
			pPrEle.add(tabsEle);
			pPrEle.add(snapToGridEle);
			
			Element tab1Ele = DocumentHelper.createElement(new QName("tab", wNs));
			Element tab2Ele = DocumentHelper.createElement(new QName("tab", wNs));
			tabsEle.add(tab1Ele);
			tabsEle.add(tab2Ele);
			
			tab1Ele.addAttribute(new QName("val", wNs), "center");
			tab1Ele.addAttribute(new QName("val", wNs), "4153");
			tab2Ele.addAttribute(new QName("val", wNs), "right");
			tab2Ele.addAttribute(new QName("val", wNs), "8306");
		}
		
		if(isType1){
			Element fontEle = DocumentHelper.createElement(new QName("font", wxNs));
			Element szEle = DocumentHelper.createElement(new QName("sz", wxNs));
			Element sz_csEle = DocumentHelper.createElement(new QName("sz-cs", wxNs));
			rPrEle.add(fontEle);
			rPrEle.add(szEle);
			rPrEle.add(sz_csEle);
			fontEle.addAttribute(new QName("val", wxNs), "Calibri");
			szEle.addAttribute(new QName("val", wxNs), "20");
			sz_csEle.addAttribute(new QName("val", wxNs), "20");
		}else{
			Element kernEle = DocumentHelper.createElement(new QName("kern", wNs));
			kernEle.addAttribute(new QName("val", wNs), "2");
			rPrEle.add(kernEle);
		}

		styleEle.addAttribute(new QName("type", wNs), "paragraph");
		styleEle.addAttribute(new QName("styleId", wNs), styleId);
		nameEle.addAttribute(new QName("val", wNs), nameVal);
		if(isType1){
			uiNameEle.addAttribute(new QName("val", wxNs), uiNameVal);
		}
		basedOnEle.addAttribute(new QName("val", wNs), "a");
		linkEle.addAttribute(new QName("val", wNs), linkVal);
		rsidEle.addAttribute(new QName("val", wNs), "008064BE");
		snapToGridEle.addAttribute(new QName("val", wNs), "off");
		
		return styleEle;
	}
	
	private Element getPict(String total) throws FileNotFoundException, DocumentException{
		
		Namespace wNs = getWNS();
		Namespace vNs = getVNS();
		Namespace oNs = getONS();
		Namespace wspNs = getWspNS();
		
		Element pict = DocumentHelper.createElement(new QName("pict", wNs));
		Element shapetype = DocumentHelper.createElement(new QName("shapetype", vNs));
		Element shape = DocumentHelper.createElement(new QName("shape", vNs));
		pict.add(shapetype);
		pict.add(shape);
		
		shapetype.addAttribute("id", "_x0000_t202");
		shapetype.addAttribute("coordsize", "21600,21600");
		shapetype.addAttribute(new QName("spt", oNs), "202");
		shapetype.addAttribute("path", "m,l,21600r21600,l21600,xe");
		
		Element stroke = DocumentHelper.createElement(new QName("stroke", vNs));
		Element path = DocumentHelper.createElement(new QName("path", vNs));
		shapetype.add(stroke);
		shapetype.add(path);
		
		stroke.addAttribute("joinstyle", "miter");
		path.addAttribute("gradientshapeok", "t");
		path.addAttribute(new QName("connecttype", oNs), "rect");
		
		shape.addAttribute("id", "_x0000_s3073");
		shape.addAttribute("type", "#_x0000_t202");
		shape.addAttribute("style", "position:absolute;margin-left:465.95pt;margin-top:43.2pt;width:37.6pt;height:15.65pt;z-index:1");
		shape.addAttribute("stroked", "f");
		
		Element textbox = DocumentHelper.createElement(new QName("textbox", vNs));
		shape.add(textbox);
		Element txbxContent = DocumentHelper.createElement(new QName("txbxContent", wNs));
		textbox.add(txbxContent);
		Element p = DocumentHelper.createElement(new QName("p", wNs));
		txbxContent.add(p);

		Element pPr = DocumentHelper.createElement(new QName("pPr", wNs));
		Element r = DocumentHelper.createElement(new QName("r", wNs));
		Element rPr = DocumentHelper.createElement(new QName("rPr", wNs));
		Element t = DocumentHelper.createElement(new QName("t", wNs));
		Element rFonts = DocumentHelper.createElement(new QName("rFonts", wNs));
		Element sz = DocumentHelper.createElement(new QName("sz", wNs));
		Element sz_cs = DocumentHelper.createElement(new QName("sz-cs", wNs));
		
		p.add(r);
		r.addAttribute(new QName("rsidR", wspNs), "00FD4016");
		r.addAttribute(new QName("rsidRPr", wspNs), "00FD4016");
		r.add(rPr);
		rPr.add(rFonts);
		rPr.add(sz);
		rPr.add(sz_cs);
		rFonts.addAttribute(new QName("hint", wspNs), "fareast");
		sz.addAttribute(new QName("val", wNs), "16");
		sz_cs.addAttribute(new QName("val", wNs), "16");
		
		pPr.add((Element) rPr.clone());
		p.add(pPr);

		this.insertFuncationVarable(p, 0, " PAGE   \\* MERGEFORMAT ");
		
		rFonts.addAttribute(new QName("hint", wspNs), "fareast");
		sz.addAttribute(new QName("val", wNs), "16");
		sz_cs.addAttribute(new QName("val", wNs), "16");
		r.add(t);
		t.setText("/" + total);
		return pict;
	}
	
	private Element getFtrElement(Element pict) throws FileNotFoundException, DocumentException{

		Namespace wNs = getWNS();
		Namespace wspNs = getWspNS();
		Element ftrEle = DocumentHelper.createElement(new QName("ftr", wNs));
		Element pEle = DocumentHelper.createElement(new QName("p", wNs));
		Element pRpEle = DocumentHelper.createElement(new QName("pRp", wNs));
		Element pStyleEle = DocumentHelper.createElement(new QName("pStyle", wNs));
		Element rEle = DocumentHelper.createElement(new QName("r", wNs));
		Element rPrEle = DocumentHelper.createElement(new QName("rPr", wNs));
		Element rFontsEle = DocumentHelper.createElement(new QName("rFonts", wNs));
		
		ftrEle.addAttribute(new QName("type", wNs), "odd");
		pEle.addAttribute(new QName("rsidR", wspNs), "008064BE");
		pEle.addAttribute(new QName("rsidRDefault", wspNs), "008064BE");
		pStyleEle.addAttribute(new QName("val", wNs), "a5");
		rFontsEle.addAttribute(new QName("hint", wNs), "fareast");
		
		ftrEle.add(pEle);
		pEle.add(pRpEle);
		pRpEle.add(pStyleEle);
		pEle.add(rEle);
		rEle.add(rPrEle);
		rPrEle.add(rFontsEle);
		rEle.add(pict);
		return ftrEle;
	}
	
	public static void main(String[] args) throws Exception {
		
		WordReader reader = new WmlParseReader("C:/Users/Angus/Desktop/APPLY00002_12.doc");
//		WmlParseBuilder builder = new WmlParseBuilder(reader);
//		builder.insertPageNumber(4, 12);
		reader.getBookmarkRange().get("ACCEPT_DATE").setText("100/03/09");
		XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream("C:/Users/Angus/Desktop/test-2.xml"),"UTF-8"));  
		writer.setEscapeText(false);  
		writer.write(reader.getDocument().asXML());  
		writer.flush();  
		writer.close();  
		System.out.println("finshed");
	}

}
