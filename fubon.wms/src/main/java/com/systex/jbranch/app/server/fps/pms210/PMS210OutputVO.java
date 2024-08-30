package com.systex.jbranch.app.server.fps.pms210;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : pms210OutputVO.java<br>
 * Author :KXJ<br>
 * Date :2016年11月14日 <br>
 * Version : 1.01 <br>
 * Editor : KXJ<br>
 * Editor Date : 2016年11月14日<br>
 */
public class PMS210OutputVO extends PagingOutputVO
{
	//操作結果標誌位
	private int flag;
	//上傳結果錯誤信息回饋
	private String errorMessage;
	
	//INSERT時返回值
	private int backResult;
	
	//主表查詢結果返回值
	private List<Map<String, Object>> resultList;

	//理專參數查詢結果返回值
	private List<Map<String, Object>> spAssList;
	private List<Map<String, Object>> spBenList;
	private List<Map<String, Object>> spLevdList;
	private List<Map<String, Object>> bsGoalList;
	private List<Map<String, Object>> spBounsList;
	private List<Map<String, Object>> newCustRateList;
	private List<Map<String, Object>> newSpBenList;
	private List<Map<String, Object>> newBsGoalList;
	private List<Map<String, Object>> spSalBenList;
	private List<Map<String, Object>> newBountyList;
	
	//主管參數查詢結果返回值
	private List<Map<String, Object>> bsAssList;
	private List<Map<String, Object>> bsAumList;
	private List<Map<String, Object>> bsPointList;
	private List<Map<String, Object>> bsPrizeList;
	private List<Map<String, Object>> bsGoalTarList;
	private List<Map<String, Object>> bsBonusList;

	//共用參數查詢結果返回值
	private List<Map<String, Object>> shortTranList;
	private List<Map<String, Object>> spMarList;
	private List<Map<String, Object>> spUnLackIndList;
	private List<Map<String, Object>> conNumList;
	private List<Map<String, Object>> disRateList;
	private List<Map<String, Object>> depositRateList;
	
	public int getBackResult()
	{
		return backResult;
	}
	public void setBackResult(int backResult)
	{
		this.backResult = backResult;
	}
	public List<Map<String, Object>> getResultList()
	{
		return resultList;
	}
	public void setResultList(List<Map<String, Object>> resultList)
	{
		this.resultList = resultList;
	}
	public List<Map<String, Object>> getSpAssList()
	{
		return spAssList;
	}
	public void setSpAssList(List<Map<String, Object>> spAssList)
	{
		this.spAssList = spAssList;
	}
	public List<Map<String, Object>> getSpBenList()
	{
		return spBenList;
	}
	public void setSpBenList(List<Map<String, Object>> spBenList)
	{
		this.spBenList = spBenList;
	}
	public List<Map<String, Object>> getSpLevdList()
	{
		return spLevdList;
	}
	public void setSpLevdList(List<Map<String, Object>> spLevdList)
	{
		this.spLevdList = spLevdList;
	}
	public List<Map<String, Object>> getBsGoalList()
	{
		return bsGoalList;
	}
	public void setBsGoalList(List<Map<String, Object>> bsGoalList)
	{
		this.bsGoalList = bsGoalList;
	}
	public List<Map<String, Object>> getSpBounsList()
	{
		return spBounsList;
	}
	public void setSpBounsList(List<Map<String, Object>> spBounsList)
	{
		this.spBounsList = spBounsList;
	}
	public List<Map<String, Object>> getNewCustRateList()
	{
		return newCustRateList;
	}
	public void setNewCustRateList(List<Map<String, Object>> newCustRateList)
	{
		this.newCustRateList = newCustRateList;
	}
	public List<Map<String, Object>> getNewSpBenList()
	{
		return newSpBenList;
	}
	public void setNewSpBenList(List<Map<String, Object>> newSpBenList)
	{
		this.newSpBenList = newSpBenList;
	}
	public List<Map<String, Object>> getNewBsGoalList()
	{
		return newBsGoalList;
	}
	public void setNewBsGoalList(List<Map<String, Object>> newBsGoalList)
	{
		this.newBsGoalList = newBsGoalList;
	}
	public List<Map<String, Object>> getSpSalBenList()
	{
		return spSalBenList;
	}
	public void setSpSalBenList(List<Map<String, Object>> spSalBenList)
	{
		this.spSalBenList = spSalBenList;
	}
	public List<Map<String, Object>> getNewBountyList()
	{
		return newBountyList;
	}
	public void setNewBountyList(List<Map<String, Object>> newBountyList)
	{
		this.newBountyList = newBountyList;
	}
	public List<Map<String, Object>> getBsAssList()
	{
		return bsAssList;
	}
	
	public List<Map<String, Object>> getShortTranList()
	{
		return shortTranList;
	}
	public void setShortTranList(List<Map<String, Object>> shortTranList)
	{
		this.shortTranList = shortTranList;
	}
	public void setBsAssList(List<Map<String, Object>> bsAssList)
	{
		this.bsAssList = bsAssList;
	}
	public List<Map<String, Object>> getBsAumList()
	{
		return bsAumList;
	}
	public void setBsAumList(List<Map<String, Object>> bsAumList)
	{
		this.bsAumList = bsAumList;
	}
	public List<Map<String, Object>> getBsPointList()
	{
		return bsPointList;
	}
	public void setBsPointList(List<Map<String, Object>> bsPointList)
	{
		this.bsPointList = bsPointList;
	}
	public List<Map<String, Object>> getBsPrizeList()
	{
		return bsPrizeList;
	}
	public void setBsPrizeList(List<Map<String, Object>> bsPrizeList)
	{
		this.bsPrizeList = bsPrizeList;
	}
	public List<Map<String, Object>> getBsGoalTarList()
	{
		return bsGoalTarList;
	}
	public void setBsGoalTarList(List<Map<String, Object>> bsGoalTarList)
	{
		this.bsGoalTarList = bsGoalTarList;
	}
	public List<Map<String, Object>> getBsBonusList()
	{
		return bsBonusList;
	}
	public void setBsBonusList(List<Map<String, Object>> bsBonusList)
	{
		this.bsBonusList = bsBonusList;
	}
	public List<Map<String, Object>> getSpMarList()
	{
		return spMarList;
	}
	public void setSpMarList(List<Map<String, Object>> spMarList)
	{
		this.spMarList = spMarList;
	}
	public List<Map<String, Object>> getSpUnLackIndList()
	{
		return spUnLackIndList;
	}
	public void setSpUnLackIndList(List<Map<String, Object>> spUnLackIndList)
	{
		this.spUnLackIndList = spUnLackIndList;
	}
	
	public List<Map<String, Object>> getConNumList()
	{
		return conNumList;
	}
	public void setConNumList(List<Map<String, Object>> conNumList)
	{
		this.conNumList = conNumList;
	}
	public List<Map<String, Object>> getDisRateList()
	{
		return disRateList;
	}
	public void setDisRateList(List<Map<String, Object>> disRateList)
	{
		this.disRateList = disRateList;
	}
	
	public List<Map<String, Object>> getDepositRateList()
	{
		return depositRateList;
	}
	public void setDepositRateList(List<Map<String, Object>> depositRateList)
	{
		this.depositRateList = depositRateList;
	}
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
}