package com.systex.jbranch.app.server.fps.iot180;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT180OutputVO extends PagingOutputVO{
	private List insInfoList;        //進件資訊(分頁)
	private List qualityChkList;     //品質檢核(分頁)
	private List newContrList;       //新契約狀態(分頁)
	private List invTargetList;      //投資型連結標的(分頁)
	private List riderDtlList;    	//附約明細(分頁)
	private List ReportDetail;
	private List insInfoTotList;        //進件資訊
	private List qualityChkTotList;     //品質檢核
	private List newContrTotList;       //新契約狀態
	private List exchRateList;			//匯率查詢
	private List resultList;	
	private Boolean flag;			//判斷有無查詢條件
	private List busDate;
	private String ReportType;     //報表類型
	
	private List totalList;   //核實-全行
	private List areaList;	  //核實-業務處
	private List regionList;  //核實-營運區
	
	private List export1ResultList;	//鍵機資訊匯出LIST
	
	
	public List getReportDetail() {
		return ReportDetail;
	}
	public void setReportDetail(List reportDetail) {
		ReportDetail = reportDetail;
	}
	public List getTotalList() {
		return totalList;
	}
	public void setTotalList(List totalList) {
		this.totalList = totalList;
	}
	public List getAreaList() {
		return areaList;
	}
	public void setAreaList(List areaList) {
		this.areaList = areaList;
	}
	public List getRegionList() {
		return regionList;
	}
	public void setRegionList(List regionList) {
		this.regionList = regionList;
	}
	public String getReportType() {
		return ReportType;
	}
	public void setReportType(String reportType) {
		ReportType = reportType;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getInsInfoList() {
		return insInfoList;
	}
	public void setInsInfoList(List insInfoList) {
		this.insInfoList = insInfoList;
	}	
	public List getQualityChkList() {
		return qualityChkList;
	}
	public void setQualityChkList(List qualityChkList) {
		this.qualityChkList = qualityChkList;
	}
	public List getNewContrList() {
		return newContrList;
	}
	public void setNewContrList(List newContrList) {
		this.newContrList = newContrList;
	}
	public List getInvTargetList() {
		return invTargetList;
	}
	public void setInvTargetList(List invTargetList) {
		this.invTargetList = invTargetList;
	}
	public List getRiderDtlList() {
		return riderDtlList;
	}
	public void setRiderDtlList(List riderDtlList) {
		this.riderDtlList = riderDtlList;
	}
	public List getInsInfoTotList() {
		return insInfoTotList;
	}
	public void setInsInfoTotList(List insInfoTotList) {
		this.insInfoTotList = insInfoTotList;
	}
	public List getQualityChkTotList() {
		return qualityChkTotList;
	}
	public void setQualityChkTotList(List qualityChkTotList) {
		this.qualityChkTotList = qualityChkTotList;
	}
	public List getNewContrTotList() {
		return newContrTotList;
	}
	public void setNewContrTotList(List newContrTotList) {
		this.newContrTotList = newContrTotList;
	}
	public List getExchRateList() {
		return exchRateList;
	}
	public void setExchRateList(List exchRateList) {
		this.exchRateList = exchRateList;
	}
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public List getBusDate() {
		return busDate;
	}
	public void setBusDate(List busDate) {
		this.busDate = busDate;
	}
	public List getExport1ResultList() {
		return export1ResultList;
	}
	public void setExport1ResultList(List export1ResultList) {
		this.export1ResultList = export1ResultList;
	}
	
}
