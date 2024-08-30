package com.systex.jbranch.app.server.fps.prd171;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD171InputVO extends PagingInputVO {
	private String COMPANY_NAME;
	private String P_TYPE;
	private String INSPRD_TYPE;
	private String INSPRD_NAME;
	private String INSPRD_ID;
	private String APPROVER;
	private String fileName;
	private String fileRealName;
	private List<Map<String, Object>> INS_ANCDOCLIST;

	public List<Map<String, Object>> getINS_ANCDOCLIST() {
		return INS_ANCDOCLIST;
	}
	public void setINS_ANCDOCLIST(List<Map<String, Object>> iNS_ANCDOCLIST) {
		INS_ANCDOCLIST = iNS_ANCDOCLIST;
	}
	public String getAPPROVER() {
		return APPROVER;
	}
	public void setAPPROVER(String aPPROVER) {
		APPROVER = aPPROVER;
	}
	public String getINSPRD_ID() {
		return INSPRD_ID;
	}
	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}
	public String getP_TYPE() {
		return P_TYPE;
	}
	public void setP_TYPE(String p_TYPE) {
		P_TYPE = p_TYPE;
	}
	public String getINSPRD_TYPE() {
		return INSPRD_TYPE;
	}
	public void setINSPRD_TYPE(String iNSPRD_TYPE) {
		INSPRD_TYPE = iNSPRD_TYPE;
	}
	public String getINSPRD_NAME() {
		return INSPRD_NAME;
	}
	public void setINSPRD_NAME(String iNSPRD_NAME) {
		INSPRD_NAME = iNSPRD_NAME;
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

	public String getCOMPANY_NAME() {
		return COMPANY_NAME;
	}

	public void setCOMPANY_NAME(String COMPANY_NAME) {
		this.COMPANY_NAME = COMPANY_NAME;
	}
}
