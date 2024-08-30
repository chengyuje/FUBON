package com.systex.jbranch.app.server.fps.org111;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class ORG111ResignMemoVO extends PagingInputVO{

	private String EMP_ID;                    //員工編號           
	private String RESIGN_HANDOVER;             //交接流程類型
	private String RESIGN_REASON;             //離職原因
	private String RESIGN_DESTINATION;        //離職去向
	private String DESTINATION_BANK_ID;       //離職至同業
	private String paramType;
	private String ptypeName;
	private String ptypeBuss;
	private String worksType;
	private String roleMaintain;
	private List resignList;
	
	public String getEMP_ID() {
		return EMP_ID;
	}
	
	public void setEMP_ID(String eMP_ID) {
		EMP_ID = eMP_ID;
	}

	public String getRESIGN_HANDOVER() {
		return RESIGN_HANDOVER;
	}

	public void setRESIGN_HANDOVER(String rESIGN_HANDOVER) {
		RESIGN_HANDOVER = rESIGN_HANDOVER;
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
	
	public String getParamType() {
		return paramType;
	}
	
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	
	public String getPtypeName() {
		return ptypeName;
	}
	
	public void setPtypeName(String ptypeName) {
		this.ptypeName = ptypeName;
	}
	
	public String getPtypeBuss() {
		return ptypeBuss;
	}
	
	public void setPtypeBuss(String ptypeBuss) {
		this.ptypeBuss = ptypeBuss;
	}
	
	public String getWorksType() {
		return worksType;
	}
	
	public void setWorksType(String worksType) {
		this.worksType = worksType;
	}
	
	public String getRoleMaintain() {
		return roleMaintain;
	}
	
	public void setRoleMaintain(String roleMaintain) {
		this.roleMaintain = roleMaintain;
	}

	public List getResignList() {
		return resignList;
	}

	public void setResignList(List resignList) {
		this.resignList = resignList;
	}
	
}
