package com.systex.jbranch.app.server.fps.iot140;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT140OutputVO extends PagingOutputVO{
	
	private List<Map<String, Object>> Bounced;
	private List<Map<String, Object>> IOT_MAIN;
	private String INS_KEYNO;
	private String TYPE;
	private boolean submit_check;
	private List<Map<String, Object>> IOT_MAINList;

	
	public List<Map<String, Object>> getIOT_MAINList() {
		return IOT_MAINList;
	}

	public void setIOT_MAINList(List<Map<String, Object>> iOT_MAINList) {
		IOT_MAINList = iOT_MAINList;
	}

	public boolean isSubmit_check() {
		return submit_check;
	}

	public void setSubmit_check(boolean submit_check) {
		this.submit_check = submit_check;
	}

	public String getINS_KEYNO() {
		return INS_KEYNO;
	}

	public void setINS_KEYNO(String iNS_KEYNO) {
		INS_KEYNO = iNS_KEYNO;
	}

	public List<Map<String, Object>> getIOT_MAIN() {
		return IOT_MAIN;
	}

	public void setIOT_MAIN(List<Map<String, Object>> iOT_MAIN) {
		IOT_MAIN = iOT_MAIN;
	}

	public List<Map<String, Object>> getBounced() {
		return Bounced;
	}

	public void setBounced(List<Map<String, Object>> bounced) {
		Bounced = bounced;
	}

	public String getTYPE() {
		return TYPE;
	}

	public void setTYPE(String tYPE) {
		TYPE = tYPE;
	}
	
	
	
	

}
