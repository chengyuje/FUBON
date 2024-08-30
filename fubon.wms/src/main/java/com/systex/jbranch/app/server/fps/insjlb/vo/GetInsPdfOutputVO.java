package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class GetInsPdfOutputVO {
	private List<? extends Map> lstPdf;
	/** lstPdf的結構
	 * 	PROD_ID		String	商品ID(資訊源的商品ID)
	 * 	PROD_NAME	String	商品標題(名稱)	用於pdf的標題列說明
	 * 	pdfType		String	檔案種類	1.條款 2.說明 3.列印用(同時含條款&說明)
	 * 	URL1		String	檔案1URL
	 * 	pdfName_1	String	檔案1檔名
	 * 	URL2		String	檔案2URL
	 * 	pdfName_2	String	檔案2檔名	
	 */
	
	private String pdfName1;
	private String pdfName2;

	public List<? extends Map> getLstPdf() {
		return lstPdf;
	}

	public void setLstPdf(List<? extends Map> lstPdf) {
		this.lstPdf = lstPdf;
	}

	public String getPdfName1() {
		return pdfName1;
	}

	public void setPdfName1(String pdfName1) {
		this.pdfName1 = pdfName1;
	}

	public String getPdfName2() {
		return pdfName2;
	}

	public void setPdfName2(String pdfName2) {
		this.pdfName2 = pdfName2;
	}
}
