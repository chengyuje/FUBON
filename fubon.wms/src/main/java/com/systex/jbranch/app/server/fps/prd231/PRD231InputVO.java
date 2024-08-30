package com.systex.jbranch.app.server.fps.prd231;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD231InputVO extends PagingInputVO {
	private String mkt_tier1;
	private String mkt_tier2;
	private String mkt_tier3;
	private String global_id;
	private String fileName;

	
	public String getMkt_tier1() {
		return mkt_tier1;
	}
	public void setMkt_tier1(String mkt_tier1) {
		this.mkt_tier1 = mkt_tier1;
	}
	public String getMkt_tier2() {
		return mkt_tier2;
	}
	public void setMkt_tier2(String mkt_tier2) {
		this.mkt_tier2 = mkt_tier2;
	}
	public String getMkt_tier3() {
		return mkt_tier3;
	}
	public void setMkt_tier3(String mkt_tier3) {
		this.mkt_tier3 = mkt_tier3;
	}
	public String getGlobal_id() {
		return global_id;
	}
	public void setGlobal_id(String global_id) {
		this.global_id = global_id;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileName() {
		return fileName;
	}
}
