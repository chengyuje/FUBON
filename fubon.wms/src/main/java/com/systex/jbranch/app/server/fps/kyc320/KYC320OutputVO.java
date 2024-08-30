package com.systex.jbranch.app.server.fps.kyc320;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class KYC320OutputVO extends PagingOutputVO{
	private List resultList;
	private List list;
	private List rolelist;
	private List AoCntLst;
	private Date sTime;
	private Date eTime;   
	private String inv;
	private String custId;
	private String KYCcustId;
	private List<Map<String, Object>> KYCList;		//KYC
	private String custAgeUnder65;

	public List getAoCntLst() {
		return AoCntLst;
	}
	public void setAoCntLst(List aoCntLst) {
		AoCntLst = aoCntLst;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getResultList() {
		return resultList;
	}
	
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getRolelist() {
		return rolelist;
	}
	public void setRolelist(List rolelist) {
		this.rolelist = rolelist;
	}
	public Date getsTime() {
		return sTime;
	}
	public void setsTime(Date sTime) {
		this.sTime = sTime;
	}
	public Date geteTime() {
		return eTime;
	}
	public void seteTime(Date eTime) {
		this.eTime = eTime;
	}
	public String getInv() {
		return inv;
	}
	public void setInv(String inv) {
		this.inv = inv;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getKYCCustId() {
		return KYCcustId;
	}
	public void setKYCCustId(String KYCcustId) {
		this.KYCcustId = KYCcustId;
	}

	public List<Map<String, Object>> getKYCList() {
		return KYCList;
	}
	public void setKYCList(List<Map<String, Object>> KYCList) {
		this.KYCList = KYCList;
	}
	public String getCustAgeUnder65() {
		return custAgeUnder65;
	}
	public void setCustAgeUnder65(String custAgeUnder65) {
		this.custAgeUnder65 = custAgeUnder65;
	}

}
