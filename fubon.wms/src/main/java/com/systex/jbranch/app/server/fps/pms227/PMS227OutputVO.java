package com.systex.jbranch.app.server.fps.pms227;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 個別商品轉介查詢OutputVO<br>
 * Comments Name : PMS227OutputVO.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月12日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月12日<br>
 */
public class PMS227OutputVO extends PagingOutputVO{
	private String errorMessage;                   //上傳結果錯誤信息反饋
	private String urlstr;
	private int flag;                              //操作結果判斷
	private List<Map<String, Object>> resultList;
	private List<Map<String, Object>> list;
	private List<Map<String, Object>> csvList;
	private List<Map<String, Object>> custList;   //用來客戶查詢存放list
	
	public int getFlag()
	{
		return flag;
	}
	public void setFlag(int flag)
	{
		this.flag = flag;
	}
	public String getErrorMessage()
	{
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}
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
	public List<Map<String, Object>> getCustList() {
		return custList;
	}
	public void setCustList(List<Map<String, Object>> custList) {
		this.custList = custList;
	}
	public String getUrlstr() {
		return urlstr;
	}
	public void setUrlstr(String urlstr) {
		this.urlstr = urlstr;
	}
}
