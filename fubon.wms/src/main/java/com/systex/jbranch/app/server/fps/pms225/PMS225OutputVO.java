package com.systex.jbranch.app.server.fps.pms225;
import java.util.List;
import java.util.Map;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : <br>
 * Comments Name : PMS225OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月11日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月11日<br>
 */
public class PMS225OutputVO extends PagingOutputVO{
	private  List<Map<String, Object>> resultList;
	private  List<Map<String, Object>> list;
	private  List<Map<String, Object>> csvList;
	private int rptType;
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getList()
	{
		return list;
	}
	public void setList(List<Map<String, Object>> list)
	{
		this.list = list;
	}
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
	}
	public int getRptType()
	{
		return rptType;
	}
	public void setRptType(int rptType)
	{
		this.rptType = rptType;
	}
}