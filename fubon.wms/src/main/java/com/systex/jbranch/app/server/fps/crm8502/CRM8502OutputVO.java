package com.systex.jbranch.app.server.fps.crm8502;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM8502OutputVO extends PagingInputVO {
	private List custAssetDocList1;				//客戶資況表申請書1
	private List custAssetDocList2;				//客戶資況表申請書2
	private List custAssetPrintHisList;			//客戶資況表列印記錄
	private String custName;					//客戶姓名
	private List result;                        //預覽列印回傳結果
	
	public List getResult() {
		return result;
	}
	public void setResult(List result) {
		this.result = result;
	}
	public List getCustAssetDocList1() {
		return custAssetDocList1;
	}
	public void setCustAssetDocList1(List custAssetDocList1) {
		this.custAssetDocList1 = custAssetDocList1;
	}
	
	public List getCustAssetDocList2() {
		return custAssetDocList2;
	}
	public void setCustAssetDocList2(List custAssetDocList2) {
		this.custAssetDocList2 = custAssetDocList2;
	}
	
	public List getCustAssetPrintHisList() {
		return custAssetPrintHisList;
	}
	public void setCustAssetPrintHisList(List custAssetPrintHisList) {
		this.custAssetPrintHisList = custAssetPrintHisList;
	}
	
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	
	
	
}
