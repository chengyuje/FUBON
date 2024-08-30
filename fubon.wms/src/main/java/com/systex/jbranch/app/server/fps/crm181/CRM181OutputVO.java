package com.systex.jbranch.app.server.fps.crm181;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM181OutputVO extends PagingOutputVO {

	private List resultList;
	private List dataCount;
	private List otherList;
	private String cus130Count;
	private String reviewCount;
	private String sqm120Count;
	private String sqm320Count;
	private String mgm115Count;
	private String crm990Count;
	private String crm8502Count;
	private String kyc320Count;
	private String pms421Count;
	private String pms429Msg;

	private Integer fetilrListCount;

	public String getCrm8502Count() {
		return crm8502Count;
	}

	public void setCrm8502Count(String crm8502Count) {
		this.crm8502Count = crm8502Count;
	}

	public String getCrm990Count() {
		return crm990Count;
	}

	public void setCrm990Count(String crm990Count) {
		this.crm990Count = crm990Count;
	}

	private List<Map<String, Object>> fetilrListW;

	public Integer getFetilrListCount() {
		return fetilrListCount;
	}

	public void setFetilrListCount(Integer fetilrListCount) {
		this.fetilrListCount = fetilrListCount;
	}

	public List<Map<String, Object>> getFetilrListW() {
		return fetilrListW;
	}

	public void setFetilrListW(List<Map<String, Object>> fetilrListW) {
		this.fetilrListW = fetilrListW;
	}

	public List getResultList() {
		return resultList;
	}

	public String getPms421Count() {
		return pms421Count;
	}

	public void setPms421Count(String pms421Count) {
		this.pms421Count = pms421Count;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getDataCount() {
		return dataCount;
	}

	public void setDataCount(List dataCount) {
		this.dataCount = dataCount;
	}

	public List getOtherList() {
		return otherList;
	}

	public void setOtherList(List otherList) {
		this.otherList = otherList;
	}

	public String getCus130Count() {
		return cus130Count;
	}

	public void setCus130Count(String cus130Count) {
		this.cus130Count = cus130Count;
	}

	public String getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(String reviewCount) {
		this.reviewCount = reviewCount;
	}

	public String getSqm120Count() {
		return sqm120Count;
	}

	public void setSqm120Count(String sqm120Count) {
		this.sqm120Count = sqm120Count;
	}

	public String getMgm115Count() {
		return mgm115Count;
	}

	public void setMgm115Count(String mgm115Count) {
		this.mgm115Count = mgm115Count;
	}

	public String getSqm320Count() {
		return sqm320Count;
	}

	public void setSqm320Count(String sqm320Count) {
		this.sqm320Count = sqm320Count;
	}
	
	public String getKyc320Count() {
		return kyc320Count;
	}

	public void setKyc320Count(String kyc320Count) {
		this.kyc320Count = kyc320Count;
	}

	public String getPms429Msg() {
		return pms429Msg;
	}

	public void setPms429Msg(String pms429Msg) {
		this.pms429Msg = pms429Msg;
	}

	//	public String getCrm990Count() {
	//		return crm990Count;
	//	}
	//
	//	public void setCrm990Count(String crm990Count) {
	//		this.crm990Count = crm990Count;
	//	}

}