package com.systex.jbranch.app.server.fps.pms330;

import java.sql.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專各級客戶數增減 OutputVO<br>
 * Comments Name : PMS330OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年05月31日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS330OutputVO extends PagingOutputVO {
	private List resultList;  //分頁結果
	private List list;		  //暫存CSV欄位
	private List csvList;	  //匯出用CSV
	private Date time;      //比較日

	public List getList() {
		return list;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public void setList(List list) {
		this.list = list;
	}

	public List getResultList() {
		return resultList;
	}

	public List getCsvList() {
		return csvList;
	}

	public void setCsvList(List csvList) {
		this.csvList = csvList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

}
