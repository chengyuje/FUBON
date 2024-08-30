package com.systex.jbranch.app.server.fps.appvo.vo;

import java.util.List;

public class CustVO {

	private String custID;//客戶ID
	private InfoVO infoVO;//客戶基本資料區
	private AssetVO assetVO;//客戶資產區
	private List warningList;//警示項目清單
	private List assetList;//資產彙總清單
	//2013/09/24新增
	private String assetListDate;//	資產彙總資料日期
	
	private List affairList;//不可不知內容清單
	private AdvanceVO advanceVO;//客戶彙總資料區
	
	//暫時保留
	private String pcFlag;
	private String education;
	private String birthDate;
	private String sickFlag;
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public InfoVO getInfoVO() {
		return infoVO;
	}
	public void setInfoVO(InfoVO infoVO) {
		this.infoVO = infoVO;
	}
	public AssetVO getAssetVO() {
		return assetVO;
	}
	public void setAssetVO(AssetVO assetVO) {
		this.assetVO = assetVO;
	}
	public List getWarningList() {
		return warningList;
	}
	public void setWarningList(List warningList) {
		this.warningList = warningList;
	}
	public List getAssetList() {
		return assetList;
	}
	public void setAssetList(List assetList) {
		this.assetList = assetList;
	}
	public String getAssetListDate() {
		return assetListDate;
	}
	public void setAssetListDate(String assetListDate) {
		this.assetListDate = assetListDate;
	}
	public List getAffairList() {
		return affairList;
	}
	public void setAffairList(List affairList) {
		this.affairList = affairList;
	}
	public AdvanceVO getAdvanceVO() {
		return advanceVO;
	}
	public void setAdvanceVO(AdvanceVO advanceVO) {
		this.advanceVO = advanceVO;
	}
	public String getPcFlag() {
		return pcFlag;
	}
	public void setPcFlag(String pcFlag) {
		this.pcFlag = pcFlag;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public String getSickFlag() {
		return sickFlag;
	}
	public void setSickFlag(String sickFlag) {
		this.sickFlag = sickFlag;
	}
}