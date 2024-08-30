package com.systex.jbranch.app.server.fps.insjlb.vo;

public class ThirdInsProdInputVO {

	private String thirdProdCode; 	// 產品ID
	private String prodName;		// 產品關鍵字
	private String insCO;			// 保險公司代碼
	private String qid;				// 商品屬性編號
	private String isMain;			// 主約/附約
	private String isOld;			// 現售/停售
	private String srfind;
	
	public ThirdInsProdInputVO() {
		super();
	}
	public String getThirdProdCode() {
		return thirdProdCode;
	}
	public void setThirdProdCode(String thirdProdCode) {
		this.thirdProdCode = thirdProdCode;
	}
	public String getProdName() {
		return prodName;
	}
	public void setProdName(String prodName) {
		this.prodName = prodName;
	}
	public String getInsCO() {
		return insCO;
	}
	public void setInsCO(String insCO) {
		this.insCO = insCO;
	}
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public String getIsMain() {
		return isMain;
	}
	public void setIsMain(String isMain) {
		this.isMain = isMain;
	}
	public String getIsOld() {
		return isOld;
	}
	public void setIsOld(String isOld) {
		this.isOld = isOld;
	}
	public String getSrfind() {
		return srfind;
	}
	public void setSrfind(String srfind) {
		this.srfind = srfind;
	}
}
