package com.systex.jbranch.app.server.fps.pms209;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 競爭力趨勢<br>
 * Comments Name :pms209InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年06月28日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */
public class PMS209ImageVO extends PagingOutputVO {
	private List resultList;
	private List DATA;
	private List list;

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

	public List getDATA() {
		return DATA;
	}

	public void setDATA(List dATA) {
		DATA = dATA;
	}

	public int getPageCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}
