package com.systex.jbranch.app.server.fps.fps340;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.fps300.FPS300InputVO;

public class FPS340InputVO extends FPS300InputVO {
	public FPS340InputVO(){}
	private boolean commRsYn;
	private String planStatus;
	private List<Map<String, Object>> prdList;
	private ByteArrayInputStream  pdfBlob;
	public ByteArrayInputStream  getPdfBlob() {
		return pdfBlob;
	}
	public void setPdfBlob(ByteArrayInputStream  pdfBlob) {
		this.pdfBlob = pdfBlob;
	}
	public boolean getCommRsYn() {
		return commRsYn;
	}
	public void setCommRsYn(boolean commRsYn) {
		this.commRsYn = commRsYn;
	}
	
	public String getPlanStatus() {
		return planStatus;
	}
	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}
	
	public List<Map<String, Object>> getPrdList() {
		return prdList;
	}
	public void setPrdList(List<Map<String, Object>> prdList) {
		this.prdList = prdList;
	}	
  
}