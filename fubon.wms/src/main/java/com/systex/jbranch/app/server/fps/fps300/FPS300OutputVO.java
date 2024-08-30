package com.systex.jbranch.app.server.fps.fps300;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * 我的客戶搜尋(FPS300OutputVO)
 * <p>
 * WMSA-TDS-FPS300-理財規劃-框架查詢.doc
 * <p>
 * Notics: 修改歷程及說明<br>
 * 
 * <pre>
 * 版本日期   IR/專案/需求單號 異動說明                      修改人員
 * ========== ================ ============================= ================
 * 
 * </pre>
 * 
 * @author KevinHsu
 */

public class FPS300OutputVO extends PagingOutputVO {
	private List<Map<String,Object>> custResulList ;		//CUST資訊
	private String step;  //導頁用	
	private List<Map<String, Object>> briefList;
	private List<Map<String, Object>> manualList;
	private List<Map<String, Object>> noticeList;
	
	public List<Map<String, Object>> getBriefList() {
		return briefList;
	}
	public void setBriefList(List<Map<String, Object>> briefList) {
		this.briefList = briefList;
	}
	public List<Map<String, Object>> getManualList() {
		return manualList;
	}
	public void setManualList(List<Map<String, Object>> manualList) {
		this.manualList = manualList;
	}
	public List<Map<String, Object>> getNoticeList() {
		return noticeList;
	}
	public void setNoticeList(List<Map<String, Object>> noticeList) {
		this.noticeList = noticeList;
	}
	public List<Map<String, Object>> getCustResulList() {
		return custResulList;
	}
	public void setCustResulList(List<Map<String, Object>> custResulList) {
		this.custResulList = custResulList;
	}
	
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}	
	
	
}
