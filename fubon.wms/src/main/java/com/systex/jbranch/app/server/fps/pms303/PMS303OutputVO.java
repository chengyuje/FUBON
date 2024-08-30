package com.systex.jbranch.app.server.fps.pms303;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :分行保險速報統計 OutputVO<br>
 * Comments Name : PMS303OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年08月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS303OutputVO extends PagingOutputVO{
	private List resultList; //主畫面查詢結果
	private List list;
	private List csvList;   //csv list
	private List AoCntLst;
	private List discountList;
	private List totalList; // 全行總計
	private List regionCenterList; // 業務處總計
	private List branchAreaList; // 區總計
	private Float rate;
	private  Date sCreDate ;// 時間 
	
	public List getDiscountList() {
		return discountList;
	}
	public void setDiscountList(List discountList) {
		this.discountList = discountList;
	}
	public Date getsCreDate() {
		return sCreDate;
	}
	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}
	public Float getRate() {
		return rate;
	}
	public void setRate(Float rate) {
		this.rate = rate;
	}
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
	public List getCsvList() {
		return csvList;
	}
	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
	public List getTotalList() {
		return totalList;
	}
	public void setRegionCenterList(List regionCenterList) {
		this.regionCenterList = regionCenterList;
	}
	public List getRegionCenterList() {
		return regionCenterList;
	}
	public void setBranchAreaList(List branchAreaList) {
		this.branchAreaList = branchAreaList;
	}
	public List getBranchAreaList() {
		return branchAreaList;
	}
}
