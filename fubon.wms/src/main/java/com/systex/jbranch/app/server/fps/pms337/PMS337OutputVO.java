package com.systex.jbranch.app.server.fps.pms337;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 保險月底提存及月中核實報表OutputVO<br>
 * Comments Name : PMS337OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年10月15日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
public class PMS337OutputVO extends PagingOutputVO{
	private List resultList;   //分頁查詢結果list
	private List list;
	private String previewType;
	
	public String getPreviewType() {
		return previewType;
	}
	public void setPreviewType(String previewType) {
		this.previewType = previewType;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

}
