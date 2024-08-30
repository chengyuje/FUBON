package com.systex.jbranch.app.server.fps.iot900;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT900InputVO extends PagingInputVO{
	
	private String IN_TYPE;
	private String INS_KEYNO;
	private String OPR_STATUS;
	private String DOC_CHK;
	private String in_REGTYPE;
	private String in_othtype;
	private String INSPRD_ID;
	private List<Map<String, Object>> inList;
	private List<Map<String, Object>> outList;
	
	
	public String getINSPRD_ID() {
		return INSPRD_ID;
	}
	public void setINSPRD_ID(String iNSPRD_ID) {
		INSPRD_ID = iNSPRD_ID;
	}
	public List<Map<String, Object>> getInList() {
		return inList;
	}
	public List<Map<String, Object>> getOutList() {
		return outList;
	}
	public void setInList(List<Map<String, Object>> inList) {
		this.inList = inList;
	}
	public void setOutList(List<Map<String, Object>> outList) {
		this.outList = outList;
	}
	public String getIn_REGTYPE() {
		return in_REGTYPE;
	}
	public void setIn_REGTYPE(String in_REGTYPE) {
		this.in_REGTYPE = in_REGTYPE;
	}
	public String getIn_othtype() {
		return in_othtype;
	}
	public void setIn_othtype(String in_othtype) {
		this.in_othtype = in_othtype;
	}
	public String getDOC_CHK() {
		return DOC_CHK;
	}
	public void setDOC_CHK(String dOC_CHK) {
		DOC_CHK = dOC_CHK;
	}
	public String getIN_TYPE() {
		return IN_TYPE;
	}
	public void setIN_TYPE(String iN_TYPE) {
		IN_TYPE = iN_TYPE;
	}
	public String getINS_KEYNO() {
		return INS_KEYNO;
	}
	public void setINS_KEYNO(String iNS_KEYNO) {
		INS_KEYNO = iNS_KEYNO;
	}
	public String getOPR_STATUS() {
		return OPR_STATUS;
	}
	public void setOPR_STATUS(String oPR_STATUS) {
		OPR_STATUS = oPR_STATUS;
	}
	
	
}
