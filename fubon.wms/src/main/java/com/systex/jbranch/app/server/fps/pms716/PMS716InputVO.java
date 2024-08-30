package com.systex.jbranch.app.server.fps.pms716;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : <br>
 * Comments Name : pms716InputVO.java<br>
 * Author :CTY<br>
 * Date :2016年12月22日 <br>
 * Version : 1.01 <br>
 * Editor : CTY<br>
 * Editor Date : 2016年11月14日<br>
 */
public class PMS716InputVO extends PagingInputVO
{
	//公用
	private String sTime;
	private String userId;
	private String fileName;
	private String storedFlag;
	
	//下載範例參數
	private String downloadSample; 
	
	//參數
	private String PAR_LIMIT;
	private String PAR_RATE;
	private String PAR_BOUNTY;
	private String PAR_PROJ;
	private String PAR_BONUS_ADJ;
	private String PAR_PS_LIST;
	
	//撥款額度定額獎金表參數
	private List<Map<String, String>>  inputList;

	public List<Map<String, String>> getInputList()
	{
		return inputList;
	}
	public void setInputList(List<Map<String, String>> inputList)
	{
		this.inputList = inputList;
	}
	public String getsTime()
	{
		return sTime;
	}
	public void setsTime(String sTime)
	{
		this.sTime = sTime;
	}
	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getFileName()
	{
		return fileName;
	}
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public String getStoredFlag()
	{
		return storedFlag;
	}
	public void setStoredFlag(String storedFlag)
	{
		this.storedFlag = storedFlag;
	}
	public String getPAR_LIMIT()
	{
		return PAR_LIMIT;
	}
	public String getPAR_RATE()
	{
		return PAR_RATE;
	}
	public void setPAR_RATE(String pAR_RATE)
	{
		PAR_RATE = pAR_RATE;
	}
	public String getPAR_BOUNTY()
	{
		return PAR_BOUNTY;
	}
	public void setPAR_BOUNTY(String pAR_BOUNTY)
	{
		PAR_BOUNTY = pAR_BOUNTY;
	}
	public void setPAR_LIMIT(String pAR_LIMIT)
	{
		PAR_LIMIT = pAR_LIMIT;
	}
	public String getPAR_PROJ()
	{
		return PAR_PROJ;
	}
	public void setPAR_PROJ(String pAR_PROJ)
	{
		PAR_PROJ = pAR_PROJ;
	}
	public String getPAR_BONUS_ADJ()
	{
		return PAR_BONUS_ADJ;
	}
	public void setPAR_BONUS_ADJ(String pAR_BONUS_ADJ)
	{
		PAR_BONUS_ADJ = pAR_BONUS_ADJ;
	}
	public String getPAR_PS_LIST()
	{
		return PAR_PS_LIST;
	}
	public void setPAR_PS_LIST(String pAR_PS_LIST)
	{
		PAR_PS_LIST = pAR_PS_LIST;
	}
	public String getDownloadSample() {
		return downloadSample;
	}
	public void setDownloadSample(String downloadSample) {
		this.downloadSample = downloadSample;
	}
	
}