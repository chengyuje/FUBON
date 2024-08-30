package com.systex.jbranch.app.server.fps.crm827;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM827OutputVO extends PagingOutputVO{
	private List resultList_data;   
	private List<CustAssetNMVIPAVO>  resultList;   
	private String errorCode;   //錯誤代碼
	private String errorMsg;    //錯誤訊息
	private	List resultList1;
	private	List resultList2;
	private	List resultList3;
	private List <Map<String, Object>> CDepList;		//台外幣活存
	private List <Map<String, Object>> TDepList;		//台幣定存
	private List <Map<String, Object>> FTDepList;		//外幣定存
	private List <Map<String, Object>> fundList;	//基金
	private String cust_id;
	private List <Map<String, Object>> ETFList;		//海外ETF
	private List <Map<String, Object>> ovsStockList;	//海外股票
	private List<Map<String, Object>> fbondList;	//海外債券
	private List<Map<String, Object>> SIList;		//SI
	private List<Map<String, Object>> SNList;		//SN
	
	public List<CustAssetNMVIPAVO> getResultList() {
		return resultList;
	}
	public void setResultList(List<CustAssetNMVIPAVO> resultList) {
		this.resultList = resultList;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public List getResultList_data() {
		return resultList_data;
	}
	public void setResultList_data(List resultList_data) {
		this.resultList_data = resultList_data;
	}
	public List getResultList1() {
		return resultList1;
	}
	public void setResultList1(List resultList1) {
		this.resultList1 = resultList1;
	}
	public List getResultList2() {
		return resultList2;
	}
	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}
	public List getResultList3() {
		return resultList3;
	}
	public void setResultList3(List resultList3) {
		this.resultList3 = resultList3;
	}
	public List<Map<String, Object>> getCDepList() {
		return CDepList;
	}
	public void setCDepList(List<Map<String, Object>> CDepList) {
		this.CDepList = CDepList;
	}
	public List<Map<String, Object>> getTDepList() {
		return TDepList;
	}
	public void setTDepList(List<Map<String, Object>> TDepList) {
		this.TDepList = TDepList;
	}
	public List<Map<String, Object>> getFTDepList() {
		return FTDepList;
	}
	public void setFTDepList(List<Map<String, Object>> FTDepList) {
		this.FTDepList = FTDepList;
	}
	public void setCust_id(String cust_id){
		this.cust_id = cust_id;
	}
	public String getCust_id(){
		return cust_id;
	}
	public List<Map<String, Object>> getFundList() {
		return fundList;
	}
	public void setFundList(List<Map<String, Object>> fundList) {
		this.fundList = fundList;
	}
	public void setETFList(List<Map<String, Object>> ETFList) {
		this.ETFList = ETFList;
	}
	public List<Map<String, Object>> getETFList() {
		return ETFList;
	}
	public void setOvsStockList(List<Map<String, Object>> ovsStockList) {
		this.ovsStockList = ovsStockList;
	}
	public List<Map<String, Object>> getOvsStockList() {
		return ovsStockList;
	}
	public void setFbondList(List<Map<String, Object>> fbondList) {
		this.fbondList = fbondList;
	}
	public List<Map<String, Object>> getFbondList() {
		return fbondList;
	}
	public void setSIList(List<Map<String, Object>> SIList) {
		this.SIList = SIList;
	}
	public List<Map<String, Object>> getSIList() {
		return SIList;
	}
	public void setSNList(List<Map<String, Object>> SNList) {
		this.SNList = SNList;
	}
	public List<Map<String, Object>> getSNList() {
		return SNList;
	}
	
}
