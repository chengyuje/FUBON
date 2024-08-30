package com.systex.jbranch.app.server.fps.pms712;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 專員信用卡業績上傳Controller<br>
 * Comments Name : PMS712.java<br>
 * Author : cty<br>
 * Date :2016年11月17日 <br>
 * Version : 1.0 <br>
 * Editor : cty<br>
 * Editor Date : 2016年11月17日<br>
 */
public class PMS712OutputVO extends PagingOutputVO
{
	private List<Map<String, Object>> resultList;   //返回結果集
	
	
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
}
