package com.systex.jbranch.app.server.fps.org150;

import java.sql.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG150InputVO extends PagingInputVO {
	
	private String region_center_id; 
	private String branch_area_id;
	private String branch_nbr;
	private String EMP_ID;
	private String EMP_NAME;
	private String RESIGN_REASON;
	private String RESIGN_DESTINATION;
	private String DESTINATION_BANK_ID;	
	private List EXPORT_LST;
	//新增日期欄位
	private Date dateS;
	private Date dateE;
	
	
	
	public Date getDateS() {
		return dateS;
	}

	public Date getDateE() {
		return dateE;
	}

	public void setDateS(Date dateS) {
		this.dateS = dateS;
	}

	public void setDateE(Date dateE) {
		this.dateE = dateE;
	}

	public List getEXPORT_LST() {
		return EXPORT_LST;
	}

	public void setEXPORT_LST(List eXPORT_LST) {
		EXPORT_LST = eXPORT_LST;
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
	
	public String getBranch_nbr() {
		return branch_nbr;
	}
	
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}
	
	public String getEMP_ID() {
		return EMP_ID;
	}
	
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}
	
	public String getEMP_NAME() {
		return EMP_NAME;
	}
	
	public void setEMP_NAME(String eMP_NAME) {
		EMP_NAME = eMP_NAME;
	}
	
	public String getRESIGN_REASON() {
		return RESIGN_REASON;
	}
	
	public void setRESIGN_REASON(String rESIGN_REASON) {
		RESIGN_REASON = rESIGN_REASON;
	}
	
	public String getRESIGN_DESTINATION() {
		return RESIGN_DESTINATION;
	}
	
	public void setRESIGN_DESTINATION(String rESIGN_DESTINATION) {
		RESIGN_DESTINATION = rESIGN_DESTINATION;
	}
	
	public String getDESTINATION_BANK_ID() {
		return DESTINATION_BANK_ID;
	}
	
	public void setDESTINATION_BANK_ID(String dESTINATION_BANK_ID) {
		DESTINATION_BANK_ID = dESTINATION_BANK_ID;
	}
}
