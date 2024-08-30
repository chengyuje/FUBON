package com.systex.jbranch.app.server.fps.crm341;

import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM341InputVO extends PagingInputVO{
	private String cust_id;
	private String org_ao_code;
	private String org_ao_brh;
	private String new_ao_code;
	private String new_ao_brh;
//	private String apl_emp_id;
//	private String apl_emp_role;
	private String apl_reason;
	private String apl_oth_reason;
	private String agmt_seq;
	private String trs_flow_type;
	private String process_status;
	private String pri_id;
	private String fileName;
	private String fileRealName;
	private Map<String, String> oldVOList;
	private Map<String, String> newVOList;
	private boolean isCMDTCust; //是否為十保客戶ID監控
	private boolean is2022CMDTCust3; //是否為2022換手客戶
	private boolean is2023CMDTCust4; //是否為2023必輪調：區域分行非核心客戶一年內移回原理專
	private String fileName2;     //客戶指定聲明書
	private String fileRealName2; //客戶指定聲明書
	private String fromCRM3103YN;
	
	public String getFileName2() {
		return fileName2;
	}
	public void setFileName2(String fileName2) {
		this.fileName2 = fileName2;
	}
	public String getFileRealName2() {
		return fileRealName2;
	}
	public void setFileRealName2(String fileRealName2) {
		this.fileRealName2 = fileRealName2;
	}
	public boolean isCMDTCust() {
		return isCMDTCust;
	}
	public void setCMDTCust(boolean isCMDTCust) {
		this.isCMDTCust = isCMDTCust;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getOrg_ao_code() {
		return org_ao_code;
	}
	public void setOrg_ao_code(String org_ao_code) {
		this.org_ao_code = org_ao_code;
	}
	public String getOrg_ao_brh() {
		return org_ao_brh;
	}
	public void setOrg_ao_brh(String org_ao_brh) {
		this.org_ao_brh = org_ao_brh;
	}
	public String getNew_ao_code() {
		return new_ao_code;
	}
	public void setNew_ao_code(String new_ao_code) {
		this.new_ao_code = new_ao_code;
	}
	public String getNew_ao_brh() {
		return new_ao_brh;
	}
	public void setNew_ao_brh(String new_ao_brh) {
		this.new_ao_brh = new_ao_brh;
	}
	public String getApl_reason() {
		return apl_reason;
	}
	public void setApl_reason(String apl_reason) {
		this.apl_reason = apl_reason;
	}
	public String getApl_oth_reason() {
		return apl_oth_reason;
	}
	public void setApl_oth_reason(String apl_oth_reason) {
		this.apl_oth_reason = apl_oth_reason;
	}
	public String getAgmt_seq() {
		return agmt_seq;
	}
	public void setAgmt_seq(String agmt_seq) {
		this.agmt_seq = agmt_seq;
	}
	public String getTrs_flow_type() {
		return trs_flow_type;
	}
	public void setTrs_flow_type(String trs_flow_type) {
		this.trs_flow_type = trs_flow_type;
	}
	public String getProcess_status() {
		return process_status;
	}
	public void setProcess_status(String process_status) {
		this.process_status = process_status;
	}
	public String getPri_id() {
		return pri_id;
	}
	public void setPri_id(String pri_id) {
		this.pri_id = pri_id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	public Map<String, String> getOldVOList() {
		return oldVOList;
	}
	public void setOldVOList(Map<String, String> oldVOList) {
		this.oldVOList = oldVOList;
	}
	public Map<String, String> getNewVOList() {
		return newVOList;
	}
	public void setNewVOList(Map<String, String> newVOList) {
		this.newVOList = newVOList;
	}
	public boolean isIs2022CMDTCust3() {
		return is2022CMDTCust3;
	}
	public void setIs2022CMDTCust3(boolean is2022cmdtCust3) {
		is2022CMDTCust3 = is2022cmdtCust3;
	}
	public boolean isIs2023CMDTCust4() {
		return is2023CMDTCust4;
	}
	public void setIs2023CMDTCust4(boolean is2023cmdtCust4) {
		is2023CMDTCust4 = is2023cmdtCust4;
	}
	public String getFromCRM3103YN() {
		return fromCRM3103YN;
	}
	public void setFromCRM3103YN(String fromCRM3103YN) {
		this.fromCRM3103YN = fromCRM3103YN;
	}
	
}