package com.systex.jbranch.app.server.fps.appvo.ins;

import java.util.List;

public class InsInvestVO {
	private String versionNo;		//版本序號
	private String insuredID;		//被保人ID
	private String moduleType;		//L-壽險；A-意外；M-醫療；C-癌症；H-重疾
	private Number shdProtectOnce;	//應有保障(一次給付)
	private Number shdProtectDaily;	//應有保障(日額給付)
	private Number nowProtectOnce;	//現有保障(一次給付)
	private Number nowProtectDaily;	//現有保障(日額給付)
	private Number gapProtectOnce;	//保障缺口(一次給付)
	private Number gapProtectDaily;	//保障缺口(日額給付)
	private Number newProtectOnce;	//新增保障(一次給付)
	private Number newProtectDaily;	//新增保障(日額給付)
	private Number lastGapOnce;		//剩餘缺口(一次給付)
	private Number lastGapDaily;	//剩餘缺口(日額給付)
	private List<InsSugProductVO> insSugProduct;//保險推薦商品資料
	
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public String getInsuredID() {
		return insuredID;
	}
	public void setInsuredID(String insuredID) {
		this.insuredID = insuredID;
	}
	public String getModuleType() {
		return moduleType;
	}
	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}
	public Number getShdProtectOnce() {
		return shdProtectOnce;
	}
	public void setShdProtectOnce(Number shdProtectOnce) {
		this.shdProtectOnce = shdProtectOnce;
	}
	public Number getShdProtectDaily() {
		return shdProtectDaily;
	}
	public void setShdProtectDaily(Number shdProtectDaily) {
		this.shdProtectDaily = shdProtectDaily;
	}
	public Number getNowProtectOnce() {
		return nowProtectOnce;
	}
	public void setNowProtectOnce(Number nowProtectOnce) {
		this.nowProtectOnce = nowProtectOnce;
	}
	public Number getNowProtectDaily() {
		return nowProtectDaily;
	}
	public void setNowProtectDaily(Number nowProtectDaily) {
		this.nowProtectDaily = nowProtectDaily;
	}
	public Number getGapProtectOnce() {
		return gapProtectOnce;
	}
	public void setGapProtectOnce(Number gapProtectOnce) {
		this.gapProtectOnce = gapProtectOnce;
	}
	public Number getGapProtectDaily() {
		return gapProtectDaily;
	}
	public void setGapProtectDaily(Number gapProtectDaily) {
		this.gapProtectDaily = gapProtectDaily;
	}
	public Number getNewProtectOnce() {
		return newProtectOnce;
	}
	public void setNewProtectOnce(Number newProtectOnce) {
		this.newProtectOnce = newProtectOnce;
	}
	public Number getNewProtectDaily() {
		return newProtectDaily;
	}
	public void setNewProtectDaily(Number newProtectDaily) {
		this.newProtectDaily = newProtectDaily;
	}
	public Number getLastGapOnce() {
		return lastGapOnce;
	}
	public void setLastGapOnce(Number lastGapOnce) {
		this.lastGapOnce = lastGapOnce;
	}
	public Number getLastGapDaily() {
		return lastGapDaily;
	}
	public void setLastGapDaily(Number lastGapDaily) {
		this.lastGapDaily = lastGapDaily;
	}
	public List<InsSugProductVO> getInsSugProduct() {
		return insSugProduct;
	}
	public void setInsSugProduct(List<InsSugProductVO> insSugProduct) {
		this.insSugProduct = insSugProduct;
	}
}