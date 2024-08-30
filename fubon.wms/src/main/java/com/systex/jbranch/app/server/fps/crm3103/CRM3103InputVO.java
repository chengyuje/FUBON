package com.systex.jbranch.app.server.fps.crm3103;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM3103InputVO extends PagingInputVO{
	private String PRJ_ID;                   //專案代號
	private List<Map<String, Object>> custList;	
	private List<Map<String, Object>> selectedMList;
	private List<Map<String, Object>> selectedSList;
	private String region_center_id;
	private String branch_area_id;
	private String bra_nbr;
	private String emp_id;
	private String ao_code;
	private String saveType;
	private String STEP_STATUS;
	private String BRH_REMARKS;
	private String RGN_REMARKS;
	private BigDecimal ROT_SEQ;
	private String REC_SEQ;
	private Timestamp REC_DATE;
	private String fileName;
	private String realfileName; 
	private boolean printAllData;
	private List<Map<String, Object>> printList;
	private String headEditType;
	private String custId;
	private String STATUS;
	private String GO_CUST_YN;
	private String AO_CODE_CHG;
	private String AO_TYPE_CHG;
	private String NEW_EMP_ID;
	private String NEW_CUST_ID;
	private String NEW_AO_CODE;
	private String AO_TYPE;
	private String CUST_ID;
	
	public String getPRJ_ID() {
		return PRJ_ID;
	}
	public void setPRJ_ID(String pRJ_ID) {
		PRJ_ID = pRJ_ID;
	}
	public List<Map<String, Object>> getCustList() {
		return custList;
	}
	public void setCustList(List<Map<String, Object>> custList) {
		this.custList = custList;
	}
	public List<Map<String, Object>> getSelectedMList() {
		return selectedMList;
	}
	public void setSelectedMList(List<Map<String, Object>> selectedMList) {
		this.selectedMList = selectedMList;
	}
	public List<Map<String, Object>> getSelectedSList() {
		return selectedSList;
	}
	public void setSelectedSList(List<Map<String, Object>> selectedSList) {
		this.selectedSList = selectedSList;
	}
	public String getRegion_center_id() {
		return region_center_id;
	}
	public void setRegion_center_id(String region_center_id) {
		this.region_center_id = region_center_id;
	}
	public String getBranch_area_id() {
		return branch_area_id;
	}
	public void setBranch_area_id(String branch_area_id) {
		this.branch_area_id = branch_area_id;
	}
	public String getBra_nbr() {
		return bra_nbr;
	}
	public void setBra_nbr(String bra_nbr) {
		this.bra_nbr = bra_nbr;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getAo_code() {
		return ao_code;
	}
	public void setAo_code(String ao_code) {
		this.ao_code = ao_code;
	}
	public String getSaveType() {
		return saveType;
	}
	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}
	public String getSTEP_STATUS() {
		return STEP_STATUS;
	}
	public void setSTEP_STATUS(String sTEP_STATUS) {
		STEP_STATUS = sTEP_STATUS;
	}
	public String getBRH_REMARKS() {
		return BRH_REMARKS;
	}
	public void setBRH_REMARKS(String bRH_REMARKS) {
		BRH_REMARKS = bRH_REMARKS;
	}
	public String getRGN_REMARKS() {
		return RGN_REMARKS;
	}
	public void setRGN_REMARKS(String rGN_REMARKS) {
		RGN_REMARKS = rGN_REMARKS;
	}
	public BigDecimal getROT_SEQ() {
		return ROT_SEQ;
	}
	public void setROT_SEQ(BigDecimal rOT_SEQ) {
		ROT_SEQ = rOT_SEQ;
	}
	public String getREC_SEQ() {
		return REC_SEQ;
	}
	public void setREC_SEQ(String rEC_SEQ) {
		REC_SEQ = rEC_SEQ;
	}
	public Timestamp getREC_DATE() {
		return REC_DATE;
	}
	public void setREC_DATE(Timestamp rEC_DATE) {
		REC_DATE = rEC_DATE;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRealfileName() {
		return realfileName;
	}
	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}
	public boolean isPrintAllData() {
		return printAllData;
	}
	public void setPrintAllData(boolean printAllData) {
		this.printAllData = printAllData;
	}
	public List<Map<String, Object>> getPrintList() {
		return printList;
	}
	public void setPrintList(List<Map<String, Object>> printList) {
		this.printList = printList;
	}
	public String getHeadEditType() {
		return headEditType;
	}
	public void setHeadEditType(String headEditType) {
		this.headEditType = headEditType;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getGO_CUST_YN() {
		return GO_CUST_YN;
	}
	public void setGO_CUST_YN(String gO_CUST_YN) {
		GO_CUST_YN = gO_CUST_YN;
	}
	public String getAO_CODE_CHG() {
		return AO_CODE_CHG;
	}
	public void setAO_CODE_CHG(String aO_CODE_CHG) {
		AO_CODE_CHG = aO_CODE_CHG;
	}
	public String getAO_TYPE_CHG() {
		return AO_TYPE_CHG;
	}
	public void setAO_TYPE_CHG(String aO_TYPE_CHG) {
		AO_TYPE_CHG = aO_TYPE_CHG;
	}
	public String getNEW_EMP_ID() {
		return NEW_EMP_ID;
	}
	public void setNEW_EMP_ID(String nEW_EMP_ID) {
		NEW_EMP_ID = nEW_EMP_ID;
	}
	public String getNEW_CUST_ID() {
		return NEW_CUST_ID;
	}
	public void setNEW_CUST_ID(String nEW_CUST_ID) {
		NEW_CUST_ID = nEW_CUST_ID;
	}
	public String getNEW_AO_CODE() {
		return NEW_AO_CODE;
	}
	public void setNEW_AO_CODE(String nEW_AO_CODE) {
		NEW_AO_CODE = nEW_AO_CODE;
	}
	public String getAO_TYPE() {
		return AO_TYPE;
	}
	public void setAO_TYPE(String aO_TYPE) {
		AO_TYPE = aO_TYPE;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	
}
