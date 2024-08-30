package com.systex.jbranch.app.server.fps.ins140;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS140InputVO extends PagingInputVO {
	/**保單健診序號*/
	private String examKeyNO;
	
	/**客戶ID**/
	private String custID;
	
	/**對象 1.本人2.包含家庭關係戶*/
	private String printType;
	
	/**家庭戶清單
	 * 主關係戶	CUST_ID
	 * 關係戶		RELATION_ID
	 * 關係戶生日	RELATION_BIRTHDAY
	 * 關係戶性別	RELATION_GENDER
	 * 關係戶種類	(1-本人 2-配偶 3-子女 4-…)	RELATION_TYPE
	 * 關係戶姓名	RELATION_NAME
	 */
	private List<Map<String , Object>> lstFamily;
	
	/**現有保單一覽表*/
	private Boolean policyDetail;
	
	/**保障項目彙總表*/
	private Boolean viewStructure;
	
	/**個人保險保障彙總表*/
	private Boolean indYearSum;
	
	/**家庭保險保障彙總表*/
	private Boolean familyYear;	
	
	/**保障項目明細表*/
	private Boolean viewSum;	
	
	/**應備費用累計不足額表*/
	private Boolean familyFeeGap;
	
	/**財務安全規劃基本資料*/
	private Boolean familyFinSecData;
	
	private List<Map<String, Object>> itemList;
	
	private List<Map<String, Object>> familyFinItemList;
	
	/**是否為本人**/
	private Boolean self;
	
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	public String getPrintType() {
		return printType;
	}
	public void setPrintType(String printType) {
		this.printType = printType;
	}
	public List<Map<String , Object>> getLstFamily() {
		return lstFamily;
	}
	public void setLstFamily(List<Map<String , Object>> lstFamily) {
		this.lstFamily = lstFamily;
	}
	public Boolean getPolicyDetail() {
		return policyDetail;
	}
	public void setPolicyDetail(Boolean policyDetail) {
		this.policyDetail = policyDetail;
	}
	public Boolean getViewStructure() {
		return viewStructure;
	}
	public void setViewStructure(Boolean viewStructure) {
		this.viewStructure = viewStructure;
	}
	public Boolean getIndYearSum() {
		return indYearSum;
	}
	public void setIndYearSum(Boolean indYearSum) {
		this.indYearSum = indYearSum;
	}
	public Boolean getFamilyYear() {
		return familyYear;
	}
	public void setFamilyYear(Boolean familyYear) {
		this.familyYear = familyYear;
	}
	public Boolean getViewSum() {
		return viewSum;
	}
	public void setViewSum(Boolean viewSum) {
		this.viewSum = viewSum;
	}
	public Boolean getFamilyFeeGap() {
		return familyFeeGap;
	}
	public void setFamilyFeeGap(Boolean familyFeeGap) {
		this.familyFeeGap = familyFeeGap;
	}
	public Boolean getFamilyFinSecData() {
		return familyFinSecData;
	}
	public void setFamilyFinSecData(Boolean familyFinSecData) {
		this.familyFinSecData = familyFinSecData;
	}
	public String getExamKeyNO() {
		return examKeyNO;
	}
	public void setExamKeyNO(String examKeyNO) {
		this.examKeyNO = examKeyNO;
	}
	public List<Map<String, Object>> getItemList() {
		return itemList;
	}
	public void setItemList(List<Map<String, Object>> itemList) {
		this.itemList = itemList;
	}
	public Boolean getSelf() {
		return self;
	}
	public void setSelf(Boolean self) {
		this.self = self;
	}
	public List<Map<String, Object>> getFamilyFinItemList() {
		return familyFinItemList;
	}
	public void setFamilyFinItemList(List<Map<String, Object>> familyFinItemList) {
		this.familyFinItemList = familyFinItemList;
	}
	
}
