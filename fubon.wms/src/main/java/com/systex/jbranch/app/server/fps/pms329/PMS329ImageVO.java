package com.systex.jbranch.app.server.fps.pms329;

import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information :  <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : AUM InputVO<br>
 * Comments Name : PMS328InputVO.java<br>
 * Author :Kevin<br>
 * Date :2016年05月31日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */
public class PMS329ImageVO extends PagingOutputVO {
	private List resultList;    
	private List DATA;   //圖專用
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
