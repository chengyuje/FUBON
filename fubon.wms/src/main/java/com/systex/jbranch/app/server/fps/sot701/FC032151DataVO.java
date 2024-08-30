package com.systex.jbranch.app.server.fps.sot701;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class FC032151DataVO extends PagingOutputVO {
	
	private String EDUCATION;
	private String CAREER;
	private String MARRAGE;
	private String CHILD_NO;
	private Date BDAY_D;
	
	public String getEDUCATION() {
		return EDUCATION;
	}
	public String getCAREER() {
		return CAREER;
	}
	public String getMARRAGE() {
		return MARRAGE;
	}
	public String getCHILD_NO() {
		return CHILD_NO;
	}
	public void setEDUCATION(String eDUCATION) {
		EDUCATION = eDUCATION;
	}
	public void setCAREER(String cAREER) {
		CAREER = cAREER;
	}
	public void setMARRAGE(String mARRAGE) {
		MARRAGE = mARRAGE;
	}
	public void setCHILD_NO(String cHILD_NO) {
		CHILD_NO = cHILD_NO;
	}
	public Date getBDAY_D() {
		return BDAY_D;
	}
	public void setBDAY_D(Date bDAY_D) {
		BDAY_D = bDAY_D;
	}	

}
