package com.systex.jbranch.app.server.fps.pms713;
import java.util.List;
import java.util.Map;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : PMS713OutputVO<br>
 * Comments Name : PMS713.java<br>
 * Author :zhouyiqiong<br>
 * Date :2017年1月6日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2017年1月6日<br>
 */
public class PMS713OutputVO extends PagingOutputVO
{
	//页面显示List
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> csvList;
	private int flag;        //上傳結果判斷
	private String errorMessage; //上傳結果錯誤信息回饋
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public int getFlag()
	{
		return flag;
	}
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	public List<Map<String, Object>> getCsvList()
	{
		return csvList;
	}
	public void setCsvList(List<Map<String, Object>> csvList)
	{
		this.csvList = csvList;
	}
	public String getErrorMessage()
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
}
