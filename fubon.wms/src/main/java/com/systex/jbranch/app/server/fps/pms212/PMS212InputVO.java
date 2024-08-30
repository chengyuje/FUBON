package com.systex.jbranch.app.server.fps.pms212;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :調整客戶收益(整批)Controller<br>
 * Comments Name : PMS212.java<br>
 * Author : cty<br>
 * Date :2016年11月23日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月23日<br>
 */
public class PMS212InputVO extends PagingInputVO {
	
	private String custID;     // 客戶ID

	private String userId;    // 用戶名
	
	private String fileName; // 上傳資料名
	
	private List<Map<String, Object>> inputList; //批量更新數據


	public String getCustID() {
		return custID;
	}

	public void setCustID(String custID) {
		this.custID = custID;
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

	public List<Map<String, Object>> getInputList() {
		return inputList;
	}

	public void setInputList(List<Map<String, Object>> inputList) {
		this.inputList = inputList;
	}
}
