package com.systex.jbranch.app.server.fps.pms338;
import java.util.List;
import java.util.Map;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 房貸壽險佣金報表OutputVO<br>
 * Comments Name : PMS338OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月15日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月15日<br>
 */
public class PMS338OutputVO extends PagingOutputVO{
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> csvList;
	private List<Map<String, Object>> detailList;
	private String type;
	private String flag;
	public String getFlag()
	{
		return flag;
	}
	public void setFlag(String flag)
	{
		this.flag = flag;
	}
	public List<Map<String, Object>> getDetailList()
	{
		return detailList;
	}
	public void setDetailList(List<Map<String, Object>> detailList)
	{
		this.detailList = detailList;
	}
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
	}
}
