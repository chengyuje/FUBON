package com.systex.jbranch.app.server.fps.pms336;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 輔銷目標達成報表OutputVO<br>
 * Comments Name : PMS336OutputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月25日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */
public class PMS336OutputVO extends PagingOutputVO {
	private List resultList;  //主查詢結果

	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
}
