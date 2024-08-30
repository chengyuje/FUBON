package com.systex.jbranch.app.server.fps.ins110;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS110InputVO extends PagingInputVO{
	
	private String INSURED_ID;
	private String INSURED_NAME;
	private String KEYNO;
	private String IS_MAIN;
	private String INSSEQ;
	private String PRD_KEYNO;
	
	public String getINSURED_ID() {
		return INSURED_ID;
	}
	public String getINSURED_NAME() {
		return INSURED_NAME;
	}
	public String getKEYNO() {
		return KEYNO;
	}
	public String getIS_MAIN() {
		return IS_MAIN;
	}
	public String getINSSEQ() {
		return INSSEQ;
	}
	public void setINSURED_ID(String iNSURED_ID) {
		INSURED_ID = iNSURED_ID;
	}
	public void setINSURED_NAME(String iNSURED_NAME) {
		INSURED_NAME = iNSURED_NAME;
	}
	public void setKEYNO(String kEYNO) {
		KEYNO = kEYNO;
	}
	public void setIS_MAIN(String iS_MAIN) {
		IS_MAIN = iS_MAIN;
	}
	public void setINSSEQ(String iNSSEQ) {
		INSSEQ = iNSSEQ;
	}
	public String getPRD_KEYNO() {
		return PRD_KEYNO;
	}
	public void setPRD_KEYNO(String pRD_KEYNO) {
		PRD_KEYNO = pRD_KEYNO;
	}
	
	
}
