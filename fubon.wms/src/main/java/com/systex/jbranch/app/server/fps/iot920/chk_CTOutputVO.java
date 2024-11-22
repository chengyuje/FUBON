package com.systex.jbranch.app.server.fps.iot920;

import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class chk_CTOutputVO extends PagingOutputVO{
	
	private String Chk_Pass;
	private String CERT_chk;
	private String TRAINING_chk;
	private Map<String, Object> empDividendCert; //招攬人員是否有分紅證照 
	
	public String getChk_Pass() {
		return Chk_Pass;
	}
	public String getCERT_chk() {
		return CERT_chk;
	}
	public String getTRAINING_chk() {
		return TRAINING_chk;
	}
	public void setChk_Pass(String chk_Pass) {
		Chk_Pass = chk_Pass;
	}
	public void setCERT_chk(String cERT_chk) {
		CERT_chk = cERT_chk;
	}
	public void setTRAINING_chk(String tRAINING_chk) {
		TRAINING_chk = tRAINING_chk;
	}
	public Map<String, Object> getEmpDividendCert() {
		return empDividendCert;
	}
	public void setEmpDividendCert(Map<String, Object> empDividendCert) {
		this.empDividendCert = empDividendCert;
	}
	
	
}
