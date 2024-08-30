package com.systex.jbranch.app.server.fps.iot131;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT131InputVO extends PagingInputVO{
	
	private String in_OPRSTATUS;
	private String in_INSKEYNO;
	private String in_RISK;
	private String up_INSKEYNO;
	private String C_SENIOR_PVAL;
	
	
	public String getUp_INSKEYNO() {
		return up_INSKEYNO;
	}
	public void setUp_INSKEYNO(String up_INSKEYNO) {
		this.up_INSKEYNO = up_INSKEYNO;
	}
	public String getIn_OPRSTATUS() {
		return in_OPRSTATUS;
	}
	public void setIn_OPRSTATUS(String in_OPRSTATUS) {
		this.in_OPRSTATUS = in_OPRSTATUS;
	}
	public String getIn_INSKEYNO() {
		return in_INSKEYNO;
	}
	public void setIn_INSKEYNO(String in_INSKEYNO) {
		this.in_INSKEYNO = in_INSKEYNO;
	}
	public String getIn_RISK() {
		return in_RISK;
	}
	public void setIn_RISK(String in_RISK) {
		this.in_RISK = in_RISK;
	}
	public String getC_SENIOR_PVAL() {
		return C_SENIOR_PVAL;
	}
	public void setC_SENIOR_PVAL(String c_SENIOR_PVAL) {
		C_SENIOR_PVAL = c_SENIOR_PVAL;
	}
		

}
