package com.systex.jbranch.app.server.fps.pms710;

import java.io.File;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :調整客戶收益(整批)Controller<br>
 * Comments Name : PMS710.java<br>
 * Author : cty<br>
 * Date :2016年12月1日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
public class PMS710InputVO extends PagingInputVO {
	private String yearMon;     // 計績年月

	private String bounty_Type;  // 名單類型
	
	private String userId;   // 用戶名
	
	private String fileName;// 上傳資料名

	public String getYearMon() {
		return yearMon;
	}

	public void setYearMon(String yearMon) {
		this.yearMon = yearMon;
	}

	public String getBounty_Type()
	{
		return bounty_Type;
	}

	public void setBounty_Type(String bounty_Type)
	{
		this.bounty_Type = bounty_Type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
}
