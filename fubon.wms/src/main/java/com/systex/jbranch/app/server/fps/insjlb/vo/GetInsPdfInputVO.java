package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;

/**取得保險百科條款與說明(url)Input VO**/
public class GetInsPdfInputVO {
	/**險種清單(資訊源的商品ID)依照順序*/
	private List<String> lstInsProd;
	
	/**檔案種類：1.條款2.說明3.列印用(同時含條款&說明)4.條款+說明*/
	private String pdfType;

	public List<String> getLstInsProd() {
		return lstInsProd;
	}
	public String getPdfType() {
		return pdfType;
	}
	public void setPdfType(String pdfType) {
		this.pdfType = pdfType;
	}
	public void setLstInsProd(List<String> lstInsProd) {
		this.lstInsProd = lstInsProd;
	}
}
