package com.systex.jbranch.app.server.fps.pms708;

import java.io.File;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 專員信用卡業績上傳Controller<br>
 * Comments Name : PMS708.java<br>
 * Author : cty<br>
 * Date :2016年11月17日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月17日<br>
 */
public class PMS708InputVO extends PagingInputVO
{
	private String yearMon; // 报表年月
	
	private String fileName;// 上傳檔案名
	
	private String userId;  // 用戶名
	
	public String getYearMon()
	{
		return yearMon;
	}

	public void setYearMon(String yearMon)
	{
		this.yearMon = yearMon;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
